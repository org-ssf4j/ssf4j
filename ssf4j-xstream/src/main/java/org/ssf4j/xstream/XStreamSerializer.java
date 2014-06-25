package org.ssf4j.xstream;

import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serializer;

import com.thoughtworks.xstream.XStream;

public class XStreamSerializer<T> implements Serializer<T> {

	protected XStream xstream;
	protected OutputStream out;
	
	public XStreamSerializer(XStream xstream, OutputStream out) {
		this.xstream = xstream;
		this.out = out;
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		out.close();
	}

	@Override
	public void write(Object object) throws IOException {
		xstream.toXML(object, out);
	}

}
