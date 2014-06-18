package org.ssf4j.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.ssf4j.Deserializer;

public class JdkDeserializer implements Deserializer {
	protected ObjectInputStream in;
	
	public JdkDeserializer(InputStream in) throws IOException {
		this.in = new ObjectInputStream(in);
	}
	
	@Override
	public Object read() throws IOException, ClassNotFoundException {
		return in.readObject();
	}

	@Override
	public <T> T read(Class<T> type) throws IOException, ClassNotFoundException {
		return type.cast(read());
	}

}
