package org.ssf4j;

import java.io.Closeable;
import java.io.IOException;

public interface Deserializer<T> extends Closeable {
	public T read() throws IOException, ClassNotFoundException;
}
