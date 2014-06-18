package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroBinarySerialization implements Serialization {

	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new AvroBinarySerializer<T>(type, out);
	}

	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}


}
