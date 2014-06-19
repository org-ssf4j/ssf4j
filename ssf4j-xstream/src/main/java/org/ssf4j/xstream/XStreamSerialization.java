package org.ssf4j.xstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

import com.thoughtworks.xstream.XStream;

/**
 * Serialization facade that uses XStream
 * @author robin
 *
 */
public class XStreamSerialization implements Serialization {

	/**
	 * The {@link XStream} instance to use for serialization/deserialization.
	 * If {@code null} then a new {@link XStream} is created for each
	 * serializer/deserializer
	 */
	protected XStream xstream;
	
	/**
	 * Create a new {@link XStreamSerialization} that uses a new default {@link XStream}
	 * for each serializer/deserializer
	 */
	public XStreamSerialization() {}
	
	/**
	 * Create a new {@link XStreamSerialization} that uses the supplied {@link XStream} for
	 * each serializer/deserializer
	 * @param xstream
	 */
	public XStreamSerialization(XStream xstream) {
		this.xstream = xstream;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException {
		return new XStreamSerializer<T>(xstream, out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException {
		return new XStreamDeserializer<T>(xstream, in, type);
	}

}
