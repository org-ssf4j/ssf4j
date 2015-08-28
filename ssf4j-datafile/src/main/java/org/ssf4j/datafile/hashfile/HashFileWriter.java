package org.ssf4j.datafile.hashfile;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class HashFileWriter<K, V> implements Closeable {
	private MessageDigestUtil mdu;
	private Serialization serde;
	private Class<K> keyType;
	private Class<V> valueType;
	private OutputStream keysOut;
	private OutputStream valuesOut;
	
	private boolean finished;
	private long offset;
	private SortedSet<HashPosition> positions;
	
	public HashFileWriter(
			Serialization serde, 
			MessageDigestUtil mdu,
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
	}
	
	public boolean put(K key, V value) throws IOException {
		if(finished)
			throw new IllegalStateException("already finished");
		
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

	public void finish() throws IOException {
		if(finished)
			return;
		finished = true;
		HashPositionList.write(positions.iterator(), keysOut);
	}

	@Override
	public void close() throws IOException {
		finish();
		keysOut.close();
		valuesOut.close();
	}
}
