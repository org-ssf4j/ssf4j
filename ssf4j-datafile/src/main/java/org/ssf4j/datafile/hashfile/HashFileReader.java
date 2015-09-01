package org.ssf4j.datafile.hashfile;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.util.List;
import java.util.Map;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.SeekingInput;
import org.ssf4j.datafile.SeekingInputInputStream;

/**
 * Class for reading a hashfile.  Hashfiles are similar to {@link Map}s, but support
 * either reading (via {@link HashFileReader}) or writing (via {@link HashFileWriter})
 * but not both.
 * @author robin
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class HashFileReader<K, V> implements Closeable {
	/**
	 * The source of message digests
	 */
	private MessageDigestUtil mdu;
	/**
	 * {@link Serialization} used for the hashfile
	 */
	private Serialization serde;
	/**
	 * The key type
	 */
	private Class<K> keyType;
	/**
	 * The value type
	 */
	private Class<V> valueType;
	/**
	 * The positions of the values in {@link #valuesIn}
	 */
	private List<HashPosition> keys;
	/**
	 * The {@link SeekingInput} from which values are read
	 */
	private SeekingInput valuesIn;
	
	/**
	 * Whether this {@link HashFileReader} is closed
	 */
	private boolean closed;
	
	/**
	 * Create a new {@link HashFileReader}
	 * @param mdu The source of message digests
	 * @param serde The {@link Serialization} for the hashfile
	 * @param keyType The key type
	 * @param valueType The value type
	 * @param keysIn {@link SeekingInput} for the key list, as written by {@link HashPositionList#write(java.util.Iterator, java.io.OutputStream)}
	 * @param valuesIn {@link SeekingInput} containing the value data
	 */
	public HashFileReader(
			MessageDigestUtil mdu, 
			Serialization serde, 
			Class<K> keyType, 
			Class<V> valueType,
			SeekingInput keysIn, 
			SeekingInput valuesIn) {
		this(mdu, serde, keyType, valueType, new HashPositionList(keysIn, mdu), valuesIn);
	}
	
	/**
	 * Create a new {@link HashFileReader}
	 * @param mdu The source of message digests
	 * @param serde The {@link Serialization} for the hashfile
	 * @param keyType The key type
	 * @param valueType The value type
	 * @param keys {@link List} of known {@link HashPosition}s
	 * @param valuesIn {@link SeekingInput} containing the value data
	 */
	public HashFileReader(
			MessageDigestUtil mdu, 
			Serialization serde, 
			Class<K> keyType, 
			Class<V> valueType,
			List<HashPosition> keys, 
			SeekingInput valuesIn) {
		this.mdu = mdu;
		this.serde = serde;
		this.keyType = keyType;
		this.valueType = valueType;
		this.keys = keys;
		this.valuesIn = valuesIn;
		
		closed = false;
	}
	
	/**
	 * Return the {@link HashPosition} of a key object, or {@code null} if not found
	 * @param key The key object
	 * @return The {@link HashPosition} for the corresponding value, or {@code null} if the key was not found
	 * @throws IOException
	 */
	public HashPosition positionOf(K key) throws IOException {
		if(closed)
			throw new IllegalStateException("already closed");
		
		DigestOutputStream dout = new DigestOutputStream(NullOutputStream.get(), mdu.createDigest());
		
		Serializer<K> kser = serde.newSerializer(dout, keyType);
		kser.write(key);
		kser.close();
		dout.close();
		byte[] khash = dout.getMessageDigest().digest();
		
		HashPosition hp = new HashPosition(khash, 0);
		int hpi = keys.indexOf(hp);
		if(hpi < 0)
			return null;
		return keys.get(hpi);
	}
	
	/**
	 * Returns whether this hashfile has a given key
	 * @param key The key to check for
	 * @return {@code true} if the key is found, {@code false} if the key is not found
	 * @throws IOException
	 */
	public boolean containsKey(K key) throws IOException {
		return positionOf(key) != null;
	}
	
	/**
	 * Returns the value associated with a given key, or {@code null} if the key is not found
	 * @param key The key
	 * @return The value associated with the key, or {@code null} if the key is not found
	 * @throws IOException
	 */
	public V get(K key) throws IOException {
		HashPosition hp = positionOf(key);
		if(hp == null)
			return null;
		
		valuesIn.seek(hp.getPosition());
		
		byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
		valuesIn.readFully(lbytes);
		
		valuesIn.seek(hp.getPosition() + ByteArrays.LENGTH_LONG);
		long vlen = ByteArrays.toLong(lbytes, 0);
		InputStream in = new SeekingInputInputStream(valuesIn, vlen);
		
		Deserializer<V> vdes = serde.newDeserializer(in, valueType);
		V value = vdes.read();
		
		return value;
	}

	/**
	 * Returns the value associated with a given hash, or {@code null} if there is no such hash
	 * @param khash The hash
	 * @return The value associated with the hash, or {@code null} if there is no such hash
	 * @throws IOException
	 */
	public V getByHash(byte[] khash) throws IOException {
		HashPosition hp = new HashPosition(khash, 0);
		int hpi = keys.indexOf(hp);
		if(hpi < 0)
			return null;
		hp = keys.get(hpi);
		
		valuesIn.seek(hp.getPosition());
		
		byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
		valuesIn.readFully(lbytes);
		
		valuesIn.seek(hp.getPosition() + ByteArrays.LENGTH_LONG);
		long vlen = ByteArrays.toLong(lbytes, 0);
		InputStream in = new SeekingInputInputStream(valuesIn, vlen);
		
		Deserializer<V> vdes = serde.newDeserializer(in, valueType);
		V value = vdes.read();
		
		return value;
	}
	
	/**
	 * Returns the value found at the given position.  Does not verify that the
	 * position is a valid position.
	 * @param pos The position in the values file
	 * @return The value at that position
	 * @throws IOException
	 */
	public V getByPosition(long pos) throws IOException {
		valuesIn.seek(pos);
		
		byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
		valuesIn.readFully(lbytes);
		
		valuesIn.seek(pos + ByteArrays.LENGTH_LONG);
		long vlen = ByteArrays.toLong(lbytes, 0);
		InputStream in = new SeekingInputInputStream(valuesIn, vlen);
		
		Deserializer<V> vdes = serde.newDeserializer(in, valueType);
		V value = vdes.read();
		
		return value;
	}
	
	public int size() {
		return keys.size();
	}
	
	@Override
	public void close() throws IOException {
		if(closed)
			return;
		closed = true;
		if(keys instanceof Closeable)
			((Closeable) keys).close();
		valuesIn.close();
	}

}
