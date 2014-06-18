package org.ssf4j;

import java.io.IOException;

public interface Deserializer {
	public Object read() throws IOException, ClassNotFoundException;
}
