package org.ssf4j;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public interface Serializer extends Flushable, Closeable {
	public void write(Object object) throws IOException;
}
