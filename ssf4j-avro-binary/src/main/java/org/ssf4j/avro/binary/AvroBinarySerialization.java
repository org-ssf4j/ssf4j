package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

/**
 * Serialization facade that uses Apache avro's binary encoder.
 * <p/>
 * Note that avro can only reliably serialize objects created from
 * avro schemas.
 * @author robin
 *
 */
public class AvroBinarySerialization implements Serialization {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> AvroBinarySerializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new AvroBinarySerializer<T>(type, out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> AvroBinaryDeserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new AvroBinaryDeserializer<T>(type, in);
	}


}
