package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.ssf4j.Deserializer;

public class AvroJsonDeserializer implements Deserializer {

	protected Decoder dec;
	
	public AvroJsonDeserializer(Schema schema, InputStream in) throws IOException {
		dec = DecoderFactory.get().jsonDecoder(schema, in);
	}
	
	@Override
	public Object read() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
