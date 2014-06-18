package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;
import org.ssf4j.jdk.JdkDeserializer;
import org.ssf4j.jdk.JdkSerializer;

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