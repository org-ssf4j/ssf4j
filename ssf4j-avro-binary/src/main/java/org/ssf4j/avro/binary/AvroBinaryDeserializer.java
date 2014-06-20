package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.ssf4j.Deserializer;

public class AvroBinaryDeserializer<T> implements Deserializer<T> {

	protected Schema schema;
	protected Class<T> type;
	protected Decoder dec;
	protected InputStream in;
	
	public AvroBinaryDeserializer(Schema schema, InputStream in, Class<T> type) throws IOException {
		this.schema = schema;
		this.in = in;
		this.type = type;
		dec = DecoderFactory.get().binaryDecoder(in, null);
	}
	
	@Override
	public T read() throws IOException {
		Object obj = new SpecificDatumReader<IndexedRecord>(schema).read(null, dec);
		
		if(type == String.class)
			obj = obj.toString();
		
		return type.cast(obj);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
