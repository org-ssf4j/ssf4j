package org.ssf4j.datafile.hashfile;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.util.List;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.SeekingInput;
import org.ssf4j.datafile.SeekingInputInputStream;

public class HashFileReader<K, V> implements Closeable {
	private MessageDigestUtil mdu;
	private Serialization serde;
	private Class<K> keyType;
	private Class<V> valueType;
	private List<HashPosition> keys;
	private SeekingInput valuesIn;
	
	private boolean closed;
	
	public HashFileReader(
			MessageDigestUtil mdu, 
			Serialization serde, 
			Class<K> keyType, 
			Class<V> valueType,
			SeekingInput keysIn, 
			SeekingInput valuesIn) {
		this(mdu, serde, keyType, valueType, new HashPositionList(keysIn, mdu), valuesIn);
	}
	
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
	
	public HashPosition position(K key) throws IOException {
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
	
	public boolean containsKey(K key) throws IOException {
		return position(key) != null;
	}
	
	public V get(K key) throws IOException {
		HashPosition hp = position(key);
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
