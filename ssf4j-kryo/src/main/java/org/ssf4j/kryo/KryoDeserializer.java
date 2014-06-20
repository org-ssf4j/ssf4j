package org.ssf4j.kryo;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class KryoDeserializer<T> implements Deserializer<T> {

	protected Kryo kryo;
	protected Input in;
	protected Class<T> type;
	
	public KryoDeserializer(Kryo kryo, InputStream in, Class<T> type) {
		this.kryo = kryo != null ? kryo : new Kryo();
		this.in = new Input(in);
		this.type = type;
	}
	
	@Override
	public T read() throws IOException {
		return kryo.readObject(in, type);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
