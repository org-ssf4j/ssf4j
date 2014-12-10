package org.ssf4j.xstream;

import java.io.IOException;
import java.io.InputStream;

import org.ssf4j.Deserializer;

import com.thoughtworks.xstream.XStream;

public class XStreamDeserializer<T> implements Deserializer<T> {

	protected XStreamSerialization serde;
	protected XStream xstream;
	protected Class<T> type;
	protected InputStream in;
	
	public XStreamDeserializer(XStreamSerialization serde, XStream xstream, InputStream in, Class<T> type) {
		this.serde = serde;
		this.xstream = xstream;
		this.in = in;
		this.type = type;
	}
	
	@Override
	public T read() throws IOException {
		serde.getLock().lock();
		try {
			return type.cast(xstream.fromXML(in));
		} finally {
			serde.getLock().unlock();
		}
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

}
