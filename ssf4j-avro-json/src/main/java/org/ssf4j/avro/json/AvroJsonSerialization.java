package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroJsonSerialization implements Serialization {
	private static Schema schema(Class<?> type) {
		try {
			Class<? extends GenericContainer> gc = type.asSubclass(GenericContainer.class);
			return gc.newInstance().getSchema();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new AvroJsonSerializer<T>(schema(type), out);
	}

	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new AvroJsonDeserializer<T>(schema(type), in);
	}


}
