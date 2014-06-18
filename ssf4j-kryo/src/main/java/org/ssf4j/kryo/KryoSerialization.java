package org.ssf4j.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

import com.esotericsoftware.kryo.Kryo;

public class KryoSerialization implements Serialization {

	protected Kryo kryo;
	
	public KryoSerialization() {}
	
	public KryoSerialization(Kryo kryo) {
		this.kryo = kryo;
	}
	
	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new KryoSerializer(kryo, out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new KryoDeserializer(kryo, in);
	}

}
