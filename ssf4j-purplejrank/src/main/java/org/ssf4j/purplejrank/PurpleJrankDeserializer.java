package org.ssf4j.purplejrank;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.purplejrank.PurpleJrankInput;
import org.ssf4j.Deserializer;

public class PurpleJrankDeserializer<T> implements Deserializer<T> {
	protected ObjectInputStream in;
	
	public PurpleJrankDeserializer(InputStream in) throws IOException {
		this.in = new PurpleJrankInput(in);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T read() throws IOException {
		try {
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
