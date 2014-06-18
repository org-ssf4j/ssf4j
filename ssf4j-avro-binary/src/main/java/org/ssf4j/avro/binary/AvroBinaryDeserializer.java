package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.ssf4j.Deserializer;

public class AvroBinaryDeserializer implements Deserializer {

	protected Decoder dec;
	
	public AvroBinaryDeserializer(InputStream in) throws IOException {
		dec = DecoderFactory.get().binaryDecoder(in, null);
	}
	
	@Override
	public Object read() throws IOException, ClassNotFoundException {
		return new GenericDatumReader<Object>().read(null, dec);
	}

	@Override
	public <T> T read(Class<T> type) throws IOException, ClassNotFoundException {
		return new ReflectDatumReader<T>(type).read(null, dec);
	}

}
