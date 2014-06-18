package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.ssf4j.Serializer;

public class AvroBinarySerializer<A> implements Serializer {
	
	protected OutputStream out;
	protected Class<A> type;
	protected Encoder enc;
	
	public AvroBinarySerializer(Class<A> type, OutputStream out) throws IOException {
		this.type = type;
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

	@SuppressWarnings("unchecked")
	@Override
	public void write(Object object) throws IOException {
		new ReflectDatumWriter<A>(type).write((A) object, enc);
	}

}
