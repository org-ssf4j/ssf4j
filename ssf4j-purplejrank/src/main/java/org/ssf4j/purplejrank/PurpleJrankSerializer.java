package org.ssf4j.purplejrank;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.purplejrank.PurpleJrankOutput;
import org.ssf4j.Serializer;

class PurpleJrankSerializer<T> implements Serializer<T> {
	protected ObjectOutputStream out;
	
	public PurpleJrankSerializer(OutputStream out) throws IOException {
		this.out = new PurpleJrankOutput(out);
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
