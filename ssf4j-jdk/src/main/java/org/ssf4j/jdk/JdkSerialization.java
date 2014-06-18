package org.ssf4j.jdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class JdkSerialization implements Serialization {

	public JdkSerialization() {
		super();
	}

	@Override
	public Serializer newSerializer(OutputStream out) throws IOException {
		return new JdkSerializer(out);
	}

	@Override
	public Deserializer newDeserializer(InputStream in) throws IOException {
		return new JdkDeserializer(in);
	}

}