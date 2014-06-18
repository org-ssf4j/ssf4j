package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {
	public Serializer newSerializer(OutputStream out) throws IOException;
	public Deserializer newDeserializer(InputStream in) throws IOException;
}
