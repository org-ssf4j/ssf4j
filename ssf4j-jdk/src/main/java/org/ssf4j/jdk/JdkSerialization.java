package org.ssf4j.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Serialization;

/**
 * Serialization facade that uses the JDK's built-in serialization
 * @author robin
 *
 */
public class JdkSerialization implements Serialization {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> JdkSerializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new JdkSerializer<T>(out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> JdkDeserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new JdkDeserializer<T>(in);
	}

	@Override
	public boolean isThreadSafe() {
		return true;
	}


}