package org.ssf4j.purplejrank;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

/**
 * Serialization facade that uses Purple Jrank
 * @author robin
 *
 */
public class PurpleJrankSerialization implements Serialization {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new PurpleJrankSerializer<T>(out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new PurpleJrankDeserializer<T>(in);
	}


}