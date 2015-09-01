package org.ssf4j.datafile.mapreduce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestOutputStream;

import org.apache.hadoop.io.RawComparator;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.hashfile.ByteArrays;
import org.ssf4j.datafile.hashfile.MessageDigestUtil;
import org.ssf4j.datafile.hashfile.NullOutputStream;

public abstract class HashfileKeyComparator<T> implements RawComparator<T> {

	protected Class<T> keyType;
	protected Serialization serde;
	
	public HashfileKeyComparator(Class<T> keyType) {
		this(keyType, Serializations.get(Serializations.AVRO_BINARY));
	}
	
	public HashfileKeyComparator(Class<T> keyType, Serialization serde) {
		this.keyType = keyType;
		this.serde = serde;
	}
	
	protected byte[] toHash(T key) {
		DigestOutputStream dout = new DigestOutputStream(NullOutputStream.get(), MessageDigestUtil.SHA1.createDigest());
		
		try {
			Serializer<T> kser = serde.newSerializer(dout, keyType);
			kser.write(key);
			kser.close();
			dout.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return dout.getMessageDigest().digest();
	}
	
	@Override
	public int compare(T o1, T o2) {
		byte[] h1 = toHash(o1);
		byte[] h2 = toHash(o2);
		return ByteArrays.BYTE_ARRAY_ORDER.compare(h1, h2);
	}

	@Override
	public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
		T o1;
		T o2;
		
		try {
			InputStream in;
			Deserializer<T> kdes;
			
			in = new ByteArrayInputStream(b1);
			in.skip(s1);
			kdes = serde.newDeserializer(in, keyType);
			o1 = kdes.read();
			kdes.close();
			
			in = new ByteArrayInputStream(b2);
			in.skip(s2);
			kdes = serde.newDeserializer(in, keyType);
			o2 = kdes.read();
			kdes.close();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return compare(o1, o2);
	}

}
