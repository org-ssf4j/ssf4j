package org.ssf4j.avro.json;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.ssf4j.Deserializer;

public class AvroJsonDeserializer<T> implements Deserializer<T> {

	protected Schema schema;
	protected Decoder dec;
	protected InputStream in;
	
	public AvroJsonDeserializer(Schema schema, InputStream in) throws IOException {
		this.schema = schema;
		this.in = in;
		dec = DecoderFactory.get().jsonDecoder(schema, in);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T read() throws IOException, ClassNotFoundException {
		return (T) new SpecificDatumReader<IndexedRecord>(schema).read(null, dec);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
