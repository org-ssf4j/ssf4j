package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroBinarySerialization<T> implements Serialization {

	protected Class<T> type;
	
	public AvroBinarySerialization() {}
	
	public AvroBinarySerialization(Class<T> type) {
		this.type = type;
	}
	
	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new AvroBinarySerializer<T>(type, out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new AvroBinaryDeserializer<T>(type, in);
	}

}
