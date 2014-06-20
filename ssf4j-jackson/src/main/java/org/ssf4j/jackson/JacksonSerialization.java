package org.ssf4j.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serialization facade that uses Jackson's JSON mapper.
 * <p/>
 * Jackson cannot serialize everything, but comes pretty close.
 * @author robin
 *
 */
public class JacksonSerialization implements Serialization {

	protected ObjectMapper mapper;
	
	public JacksonSerialization() {}
	
	public JacksonSerialization(ObjectMapper mapper) {
		this.mapper = mapper;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> JacksonSerializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new JacksonSerializer<T>(out, mapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> JacksonDeserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new JacksonDeserializer<T>(in, mapper, type);
	}

}
