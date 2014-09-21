package org.ssf4j.kryo;

import java.io.IOException;

import org.ssf4j.ObjectDataOutput;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.KryoDataOutput;
import com.esotericsoftware.kryo.io.Output;

public class KryoObjectDataOutput extends KryoDataOutput implements ObjectDataOutput {

	protected Kryo kryo;
	
	public KryoObjectDataOutput(Kryo kryo, Output output) {
		super(output);
		this.kryo = kryo;
	}

	@Override
	public void write(Object object) throws IOException {
		write(false, object);
	}

	@Override
	public void write(boolean nulls, Object object) throws IOException {
		write(nulls, false, object);
	}

	@Override
	public void write(boolean nulls, boolean polymorphic, Object object)
			throws IOException {
		if(object instanceof Class<?>) {
			kryo.writeClass(output, (Class) object);
			return;
		}
		if(!nulls && !polymorphic)
			kryo.writeObject(output, object);
		else if(!nulls && polymorphic) {
			kryo.writeClass(output, object.getClass());
			kryo.writeObject(output, object);
		} else if(nulls && !polymorphic) {
			output.writeByte(object == null ? Kryo.NULL : Kryo.NOT_NULL);
			if(object != null)
				kryo.writeObject(output, object);
		} else
			kryo.writeClassAndObject(output, object);
	}

}
