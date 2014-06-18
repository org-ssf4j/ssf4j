package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroJsonSerialization implements Serialization {
	private static Schema schema(Class<?> type) {
		Object obj = null;
		
		if(type == boolean.class || type == Boolean.class)
			obj = true;
		else if(type == char.class || type == Character.class)
			obj = 'T';
		else if(type == double.class || type == Double.class)
			obj = 1d;
		else if(type == float.class || type == Float.class)
			obj = 1f;
		else if(type == int.class || type == Integer.class)
			obj = 1;
		else if(type == long.class || type == Long.class)
			obj = 1L;
		else if(type == short.class || type == Short.class)
			obj = (short) 1;
		
		try {
			if(obj == null && type != null)
				obj = type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			return GenericData.get().induce(obj);
		} catch (Exception e) {
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
		return new AvroJsonDeserializer<T>(schema(type), in, type);
	}


}
