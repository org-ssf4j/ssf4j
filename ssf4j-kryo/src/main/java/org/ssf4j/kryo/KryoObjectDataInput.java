package org.ssf4j.kryo;

import java.io.IOException;

import org.ssf4j.ObjectDataInput;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.KryoDataInput;

public class KryoObjectDataInput extends KryoDataInput implements ObjectDataInput {

	protected Kryo kryo;
	
	public KryoObjectDataInput(Kryo kryo, Input input) {
		super(input);
		this.kryo = kryo;
	}

	@Override
	public <T> T read(Class<T> type) throws IOException {
		return read(false, type);
	}

	@Override
	public <T> T read(boolean nulls, Class<T> type) throws IOException {
		return read(nulls, false, type);
	}

	@Override
	public <T> T read(boolean nulls, boolean polymorphic, Class<T> type)
			throws IOException {
		if(Class.class.isAssignableFrom(type))
			return type.cast(kryo.readClass(input).getType());
		if(!nulls && !polymorphic)
			return kryo.readObject(input, type);
		else if(!nulls && polymorphic) {
			Class<? extends T> t = ((Class<?>) kryo.readClass(input).getType()).asSubclass(type);
			return kryo.readObject(input, t);
		} else if(nulls && !polymorphic) {
			if(input.readByte() == Kryo.NULL)
				return null;
			return kryo.readObject(input, type);
		} else
			return type.cast(kryo.readClassAndObject(input));
	}

}
