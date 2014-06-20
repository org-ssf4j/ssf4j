package org.ssf4j.avro.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.ssf4j.Serialization;

/**
 * Serialization facade that uses Apache avro's binary encoder.
 * <p/>
 * Note that avro can only reliably serialize objects created from
 * avro schemas.
 * @author robin
 *
 */
public class AvroBinarySerialization implements Serialization {
	private static Schema schema(Class<?> type) {
		Object obj = null;
		
		if(type == boolean.class || type == Boolean.class)
			obj = true;
		else if(type == char.class || type == Character.class)
			obj = 'T';
		else if(type == double.class || type == Double.class)
			obj = 1d;
		else if(type == float.class || type == Float.class)
			obj = 1f;
		else if(type == int.class || type == Integer.class)
			obj = 1;
		else if(type == long.class || type == Long.class)
			obj = 1L;
		else if(type == short.class || type == Short.class)
			obj = (short) 1;
		
		try {
			if(obj == null && type != null)
				obj = type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		try {
			return GenericData.get().induce(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> AvroBinarySerializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new AvroBinarySerializer<T>(schema(type), out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> AvroBinaryDeserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new AvroBinaryDeserializer<T>(schema(type), in, type);
	}


}
