package org.ssf4j.kryo;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class KryoDeserializer<T> implements Deserializer<T> {

	protected KryoSerialization serde;
	protected Kryo kryo;
	protected Input in;
	protected Class<T> type;
	
	public KryoDeserializer(KryoSerialization serde, Kryo kryo, InputStream in, Class<T> type) {
		this.serde = serde;
		this.kryo = kryo;
		this.in = new Input(in);
		this.type = type;
	}
	
	@Override
	public T read() throws IOException {
		serde.getLock().lock();
		try {
			return kryo.readObject(in, type);
		} finally {
			serde.getLock().unlock();
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
