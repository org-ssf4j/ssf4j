package org.ssf4j.jackson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

/**
 * Serialization facade that uses Jackson's JSON mapper.
 * <p/>
 * Jackson cannot serialize everything, but comes pretty close.
 * @author robin
 *
 */
public class JacksonSerialization implements Serialization {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new JacksonSerializer<T>(out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new JacksonDeserializer<T>(in, type);
	}

}
