package org.ssf4j;

import java.io.IOException;

public interface Deserializer<T> {
	public T read() throws IOException, ClassNotFoundException;
}
