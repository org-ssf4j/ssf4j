package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.ssf4j.Serializer;

public class AvroBinarySerializer implements Serializer {
	
	protected Encoder enc;
	
	public AvroBinarySerializer(OutputStream out) throws IOException {
		enc = EncoderFactory.get().binaryEncoder(out, null);
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
