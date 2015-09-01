package org.ssf4j.datafile.hashfile;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

/**
 * Class for writing a hashfile.  Hashfiles are similar to {@link Map}s, but support
 * either reading (via {@link HashFileReader}) or writing (via {@link HashFileWriter})
 * but not both.
 * @author robin
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class HashFileWriter<K, V> implements Closeable {
	/**
	 * The source of message digests
	 */
	private MessageDigestUtil mdu;
	/**
	 * The {@link Serialization} for the hashfile
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
	 * {@link OutputStream} to write key data to
	 */
	private OutputStream keysOut;
	/**
	 * {@link OutputStream} to write value data to
	 */
	private OutputStream valuesOut;
	
	/**
	 * Whether {@link #finish()} has been called
	 */
	private boolean finished;
	/**
	 * The current offset in the value output stream
	 */
	private long offset;
	/**
	 * The {@link HashPosition}s for objects written so far
	 */
	private SortedSet<HashPosition> positions;
	
	/**
	 * The last {@link HashPosition} written by a call to {@link #writePresorted(Object, Object)}
	 */
	private HashPosition lastWrite;
	
	/**
	 * Create a new {@link HashFileWriter}
	 * @param mdu The source of message digests
	 * @param serde The {@link Serialization} for the hashfile
	 * @param keyType The key type
	 * @param valueType The value type
	 * @param keysOut {@link OutputStream} for key data
	 * @param valuesOut {@link OutputStream} for value data
	 */
	public HashFileWriter(
			MessageDigestUtil mdu,
			Serialization serde, 
			Class<K> keyType, 
			Class<V> valueType,
			OutputStream keysOut,
			OutputStream valuesOut) {
		this.serde = serde;
		this.keyType = keyType;
		this.valueType = valueType;
		this.keysOut = keysOut;
		this.valuesOut = valuesOut;
		
		finished = false;
		offset = 0;
		positions = new TreeSet<HashPosition>();
		lastWrite = null;
	}
	
	/**
	 * Store a key/value pair in this hashfile.  Writes the value data,
	 * and stores the value offset in a sorted set with the key hash.
	 * If a key with this hash has already been written, no value data
	 * is written and {@code false} is returned. {@code true} is returned
	 * on success.  
	 * @param key The key
	 * @param value The value
	 * @return {@code true} if the value was added, {@code false} if a key with that hash already existed
	 * @throws IOException
	 */
	public boolean put(K key, V value) throws IOException {
		if(finished)
			throw new IllegalStateException("already finished");
		
		if(lastWrite != null)
			throw new IllegalStateException("cannot call put after write");
		
		DigestOutputStream dout = new DigestOutputStream(NullOutputStream.get(), mdu.createDigest());
		
		Serializer<K> kser = serde.newSerializer(dout, keyType);
		kser.write(key);
		kser.close();
		dout.close();
		byte[] khash = dout.getMessageDigest().digest();

		HashPosition hp = new HashPosition(khash, offset);
		if(!positions.add(hp))
			return false;
		
		CountingOutputStream cout = new CountingOutputStream(NullOutputStream.get());
		
		Serializer<V> vser = serde.newSerializer(cout, valueType);
		vser.write(value);
		vser.close();
		long vlen = cout.getLength();
		
		byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
		ByteArrays.toBytes(lbytes, 0, vlen);
		valuesOut.write(lbytes);

		vser = serde.newSerializer(valuesOut, valueType);
		vser.write(value);
		vser.close();
		
		offset += lbytes.length + vlen;
		
		return true;
	}
	
	/**
	 * Write, without buffering, a key/value pair.  If {@link #writePresorted(Object, Object)}
	 * has been called already, the hashed value of the key must be greater than
	 * the hashed value of the previous key.  Can only be called if no calls to {@link #put(Object, Object)}
	 * have been made.
	 * @param key The key
	 * @param value The value
	 * @throws IOException
	 */
	public void writePresorted(K key, V value) throws IOException {
		if(finished)
			throw new IllegalStateException("already finished");
		
		if(positions.size() > 0)
			throw new IllegalStateException("cannot call write after put");
		
		DigestOutputStream dout = new DigestOutputStream(NullOutputStream.get(), mdu.createDigest());
		
		Serializer<K> kser = serde.newSerializer(dout, keyType);
		kser.write(key);
		kser.close();
		dout.close();
		byte[] khash = dout.getMessageDigest().digest();

		HashPosition hp = new HashPosition(khash, offset);
		if(lastWrite != null && HashPosition.HASH_POSITION_ORDER.compare(lastWrite, hp) >= 0)
			throw new IllegalArgumentException("non-ascending key hashes");
		lastWrite = hp;
		keysOut.write(hp.getPersisted());
		
		CountingOutputStream cout = new CountingOutputStream(NullOutputStream.get());
		
		Serializer<V> vser = serde.newSerializer(cout, valueType);
		vser.write(value);
		vser.close();
		long vlen = cout.getLength();
		
		byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
		ByteArrays.toBytes(lbytes, 0, vlen);
		valuesOut.write(lbytes);

		vser = serde.newSerializer(valuesOut, valueType);
		vser.write(value);
		vser.close();
		
		offset += lbytes.length + vlen;
	}

	/**
	 * Finish writing this hashfile by dumping all the value positions to the key outputs stream
	 * @throws IOException
	 */
	public void finish() throws IOException {
		if(finished)
			return;
		finished = true;
		if(lastWrite == null)
			HashPositionList.write(positions.iterator(), keysOut);
	}

	@Override
	public void close() throws IOException {
		finish();
		keysOut.close();
		valuesOut.close();
	}
}
