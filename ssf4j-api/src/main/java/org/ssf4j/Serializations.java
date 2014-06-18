package org.ssf4j;

public class Serializations {
	public static final String AVRO_BINARY = "org.ssf4j.avro.binary.AvroBinarySerialization";
	public static final String AVRO_JSON = "org.ssf4j.avro.json.AvroJsonSerialization";
	public static final String JACKSON = "org.ssf4j.jackson.JacksonSerialization";
	public static final String JDK = "org.ssf4j.jdk.JdkSerialization";
	public static final String KRYO = "org.ssf4j.kryo.KryoSerialization";
	public static final String PURPLEJRANK = "org.ssf4j.purplejrank.PurpleJrankSerialization";
	
	public static Serialization get(String name) {
		try {
			return Class.forName(name).asSubclass(Serialization.class).newInstance();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
