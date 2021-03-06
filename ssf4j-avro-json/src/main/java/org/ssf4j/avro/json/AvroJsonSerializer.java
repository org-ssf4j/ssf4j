package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.ssf4j.Serializer;

public class AvroJsonSerializer<T> implements Serializer<T> {
	
	protected OutputStream out;
	protected Schema schema;
	protected Encoder enc;
	
	public AvroJsonSerializer(Schema schema, OutputStream out) throws IOException {
		this.schema = schema;
		this.out = out;
		enc = EncoderFactory.get().jsonEncoder(schema, out);
	}

	@Override
	public void flush() throws IOException {
		enc.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	@Override
	public void write(T object) throws IOException {
		new SpecificDatumWriter<T>(schema).write(object, enc);
	}

}
