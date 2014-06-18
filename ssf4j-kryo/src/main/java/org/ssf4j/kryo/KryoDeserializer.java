package org.ssf4j.kryo;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class KryoDeserializer implements Deserializer {

	protected Kryo kryo;
	protected Input in;
	
	public KryoDeserializer(InputStream in) {
		this.kryo = new Kryo();
		this.in = new Input(in);
	}
	
	@Override
	public Object read() throws IOException, ClassNotFoundException {
		return kryo.readClassAndObject(in);
	}

}
