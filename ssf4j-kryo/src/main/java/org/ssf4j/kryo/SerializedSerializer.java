package org.ssf4j.kryo;

import java.io.IOException;

import org.ssf4j.Serialized;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SerializedSerializer<T extends Serialized> extends Serializer<T> {

	@Override
	public void write(Kryo kryo, Output output, T object) {
		try {
			object.write(new KryoObjectDataOutput(kryo, output));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type) {
		T object = kryo.newInstance(type);
		try {
			object.read(new KryoObjectDataInput(kryo, input));
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		return object;
	}

}
