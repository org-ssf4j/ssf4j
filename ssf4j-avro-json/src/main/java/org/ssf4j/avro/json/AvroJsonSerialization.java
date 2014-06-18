package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class AvroJsonSerialization implements Serialization {

	protected Schema schema;
	
	public AvroJsonSerialization(Schema schema) {
		this.schema = schema;
	}
	
	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new AvroJsonSerializer(schema, out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new AvroJsonDeserializer(schema, in);
	}

}
