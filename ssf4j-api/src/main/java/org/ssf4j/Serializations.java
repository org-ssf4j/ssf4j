package org.ssf4j;

/**
 * Factory class that returns new instances of various known {@link Serialization}s
 * @author robin
 *
 */
public class Serializations {
	/**
	 * Apache avro, binary output format
	 */
	public static final String AVRO_BINARY = "org.ssf4j.avro.binary.AvroBinarySerialization";
	/**
	 * Apache avro, JSON output format
	 */
	public static final String AVRO_JSON = "org.ssf4j.avro.json.AvroJsonSerialization";
	/**
	 * Jackson, JSON output format
	 */
	public static final String JACKSON = "org.ssf4j.jackson.JacksonSerialization";
	/**
	 * JDK, binary output format
	 */
	public static final String JDK = "org.ssf4j.jdk.JdkSerialization";
	/**
	 * Kryo, binary output format
	 */
	public static final String KRYO = "org.ssf4j.kryo.KryoSerialization";
	/**
	 * Purple Jrank, binary output format
	 */
	public static final String PURPLEJRANK = "org.ssf4j.purplejrank.PurpleJrankSerialization";
	/**
	 * XStream, XML output format
	 */
	public static final String XSTREAM = "org.ssf4j.xstream.XStreamSerialization";
	
	/**
	 * Create a new {@link Serialization} instance from the argument classname
	 * @param name
	 * @return The new instance
	 * @see #AVRO_BINARY
	 * @see #AVRO_JSON
	 * @see #JACKSON
	 * @see #JDK
	 * @see #KRYO
	 * @see #PURPLEJRANK
	 * @see #XSTREAM
	 */
	public static Serialization get(final String name) {
		try {
			Class<? extends Serialization> serdec = Class.forName(name).asSubclass(Serialization.class);
			return serdec.newInstance();
		} catch(Exception e) {
			throw Exceptions.runtime(e);
		}
	}
	
	private Serializations() {}
}
