package org.ssf4j.jackson;

import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

class JacksonSerializer<T> implements Serializer<T> {

	protected OutputStream out;
	protected ObjectMapper mapper;
	
	public JacksonSerializer(OutputStream out) throws IOException {
		this.out = out;
		mapper = new ObjectMapper();
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	@Override
	public void write(T object) throws IOException {
		mapper.writeValue(out, object);
	}

}
