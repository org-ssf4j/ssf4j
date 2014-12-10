package org.ssf4j.jackson;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDeserializer<T> implements Deserializer<T> {

	protected JacksonSerialization serde;
	protected InputStream in;
	protected Class<T> type;
	protected ObjectMapper mapper;
	
	public JacksonDeserializer(JacksonSerialization serde, InputStream in, ObjectMapper mapper, Class<T> type) {
		this.serde = serde;
		this.in = in;
		this.type = type;
		this.mapper = mapper != null ? mapper : new ObjectMapper();
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public T read() throws IOException {
		serde.getLock().lock();
		try {
			return mapper.readValue(in, type);
		} finally {
			serde.getLock().unlock();
		}
	}

}
