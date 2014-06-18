package org.ssf4j.kryo;

import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

class KryoSerializer<T> implements Serializer<T> {

	protected Kryo kryo;
	protected Output out;
	
	public KryoSerializer(Kryo kryo, OutputStream out) {
		this.kryo = kryo != null ? kryo : new Kryo();
		this.out = new Output(out);
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
	public void write(Object object) throws IOException {
		kryo.writeObject(out, object);
	}

}
