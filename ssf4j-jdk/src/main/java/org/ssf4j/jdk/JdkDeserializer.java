package org.ssf4j.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.ssf4j.Deserializer;

public class JdkDeserializer<T> implements Deserializer<T> {
	protected ObjectInputStream in;
	
	public JdkDeserializer(InputStream in) throws IOException {
		this.in = new ObjectInputStream(in);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T read() throws IOException {
		try{
			return (T) in.readObject();
		} catch(ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
