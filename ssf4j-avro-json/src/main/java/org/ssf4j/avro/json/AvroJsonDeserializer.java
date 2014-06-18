package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.ssf4j.Deserializer;

public class AvroJsonDeserializer implements Deserializer {

	protected Schema schema;
	protected Decoder dec;
	
	public AvroJsonDeserializer(Schema schema, InputStream in) throws IOException {
		this.schema = schema;
		dec = DecoderFactory.get().jsonDecoder(schema, in);
	}
	
	@Override
	public Object read() throws IOException, ClassNotFoundException {
		return new SpecificDatumReader<IndexedRecord>(schema).read(null, dec);
	}

}
