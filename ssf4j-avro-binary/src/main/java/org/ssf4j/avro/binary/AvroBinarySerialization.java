package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroBinarySerialization<A> implements Serialization {

	protected Class<A> type;
	
	public AvroBinarySerialization() {}
	
	public AvroBinarySerialization(Class<A> type) {
		this.type = type;
	}
	
	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new AvroBinarySerializer<A>(type, out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new AvroBinaryDeserializer<A>(type, in);
	}

}
