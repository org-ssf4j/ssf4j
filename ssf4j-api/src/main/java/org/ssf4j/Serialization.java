package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException;
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException;
}
