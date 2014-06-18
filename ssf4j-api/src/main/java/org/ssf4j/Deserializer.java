package org.ssf4j;

import java.io.IOException;

public interface Deserializer {
	public Object read() throws IOException, ClassNotFoundException;
	public <T> T read(Class<T> type) throws IOException, ClassNotFoundException;
}
