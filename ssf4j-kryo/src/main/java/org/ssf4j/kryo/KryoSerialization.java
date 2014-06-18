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
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException {
		return new KryoSerializer<T>(kryo, out);
	}

	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException {
		return new KryoDeserializer<T>(kryo, in, type);
	}

}
