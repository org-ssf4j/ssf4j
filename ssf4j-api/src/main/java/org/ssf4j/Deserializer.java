package org.ssf4j;

import java.io.Closeable;
import java.io.IOException;

/**
 * Facade interface for deserializers
 * @author robin
 *
 * @param <T> The type that this deserializer can deserialize
 */
public interface Deserializer<T> extends Closeable {
	/**
	 * Deserialize a new instance of {@code T}
	 * @return The deserialized object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public T read() throws IOException, ClassNotFoundException;
}
