package org.ssf4j.xstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

import com.thoughtworks.xstream.XStream;

public class XStreamSerialization implements Serialization {

	protected XStream xstream;
	
	public XStreamSerialization() {}
	
	public XStreamSerialization(XStream xstream) {
		this.xstream = xstream;
	}
	
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException {
		return new XStreamSerializer<T>(xstream, out);
	}

	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException {
		return new XStreamDeserializer<T>(xstream, in, type);
	}

}
