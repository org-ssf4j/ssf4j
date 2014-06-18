package org.ssf4j.jdk;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.ssf4j.Serializer;

class JdkSerializer<T> implements Serializer<T> {
	protected ObjectOutputStream out;
	
	public JdkSerializer(OutputStream out) throws IOException {
		this.out = new ObjectOutputStream(out);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	@Override
	public void write(T object) throws IOException {
		out.writeObject(object);
	}

}
