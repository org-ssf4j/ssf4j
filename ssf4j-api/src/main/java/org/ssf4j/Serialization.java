package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Facade interface for serialization mechanisms.
 * @author robin
 *
 */
public interface Serialization {
	/**
	 * Create a new {@link Serializer} that serializes objects of type {@code T}
	 * @param out An {@link OutputStream} to write to
	 * @param type The {@link Class} of the objects to write
	 * @return A new {@link Serializer}
	 * @throws IOException
	 */
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException;
	/**
	 * Create a new {@link Deserializer} that deserializes objects of type {@code T}
	 * @param in An {@link InputStream} to read from
	 * @param type The {@link Class} of the objects to read
	 * @return A new {@link Deserializer}
	 * @throws IOException
	 */
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException;
}
