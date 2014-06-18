package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.ssf4j.Serializer;

public class AvroBinarySerializer implements Serializer {
	
	protected OutputStream out;
	protected Encoder enc;
	
	public AvroBinarySerializer(OutputStream out) throws IOException {
		this.out = out;
		enc = EncoderFactory.get().binaryEncoder(out, null);
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
	public void write(Object object) throws IOException {
		new ReflectDatumWriter<Object>().write(object, enc);
	}

}
