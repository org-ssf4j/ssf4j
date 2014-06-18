package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;

import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.ssf4j.Deserializer;

public class AvroBinaryDeserializer<T> implements Deserializer<T> {

	protected Class<T> type;
	protected Decoder dec;
	
	public AvroBinaryDeserializer(Class<T> type, InputStream in) throws IOException {
		this.type = type;
		dec = DecoderFactory.get().binaryDecoder(in, null);
	}
	
	@Override
	public T read() throws IOException, ClassNotFoundException {
		if(type == null)
			throw new UnsupportedOperationException();
		return new ReflectDatumReader<T>(type).read(null, dec);
	}

}
