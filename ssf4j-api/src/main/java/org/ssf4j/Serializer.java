package org.ssf4j;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * Facade interface for serializers
 * @author robin
 *
 * @param <T> The type to be serialized
 */
public interface Serializer<T> extends Flushable, Closeable {
	/**
	 * Serialize an instance of {@code T}
	 * @param object The object to serialize
	 * @throws IOException
	 */
	public void write(T object) throws IOException;
}
