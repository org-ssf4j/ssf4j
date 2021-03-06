package org.ssf4j.kryo;

import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer<T> implements Serializer<T> {

	protected KryoSerialization serde;
	protected Kryo kryo;
	protected Output out;
	
	public KryoSerializer(KryoSerialization serde, Kryo kryo, OutputStream out) {
		this.serde = serde;
		this.kryo = kryo;
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
		serde.getLock().lock();
		try {
			kryo.writeObject(out, object);
		} finally {
			serde.getLock().unlock();
		}
	}

}
