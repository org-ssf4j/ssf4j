package org.ssf4j.jackson;

import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer<T> implements Serializer<T> {

	protected JacksonSerialization serde;
	protected OutputStream out;
	protected ObjectMapper mapper;
	
	public JacksonSerializer(JacksonSerialization serde, OutputStream out, ObjectMapper mapper) throws IOException {
		this.serde = serde;
		this.out = out;
		this.mapper = mapper != null ? mapper : new ObjectMapper();
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
		serde.getLock().lock();
		try {
			mapper.writeValue(out, object);
		} finally {
			serde.getLock().unlock();
		}
	}

}
