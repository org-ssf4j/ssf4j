package org.ssf4j.xstream;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.thoughtworks.xstream.XStream;

public class XStreamDeserializer<T> implements Deserializer<T> {

	protected XStream xstream;
	protected Class<T> type;
	protected InputStream in;
	
	public XStreamDeserializer(XStream xstream, InputStream in, Class<T> type) {
		this.xstream = xstream != null ? xstream : new XStream();
		this.in = in;
		this.type = type;
	}
	
	@Override
	public T read() throws IOException, ClassNotFoundException {
		return type.cast(xstream.fromXML(in));
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
