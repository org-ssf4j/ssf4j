package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;
import org.ssf4j.kryo.KryoDeserializer;
import org.ssf4j.kryo.KryoSerializer;

public class KryoSerialization implements Serialization {

	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new KryoSerializer(out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new KryoDeserializer(in);
	}

}
