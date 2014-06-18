package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroBinarySerialization implements Serialization {

	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new AvroBinarySerializer(out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new AvroBinaryDeserializer(in);
	}

}
