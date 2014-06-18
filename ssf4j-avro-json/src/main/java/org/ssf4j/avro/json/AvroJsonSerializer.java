package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.ssf4j.Serializer;

public class AvroJsonSerializer implements Serializer {
	
	protected Encoder enc;
	
	public AvroJsonSerializer(Schema schema, OutputStream out) throws IOException {
		enc = EncoderFactory.get().jsonEncoder(schema, out);
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(Object object) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
