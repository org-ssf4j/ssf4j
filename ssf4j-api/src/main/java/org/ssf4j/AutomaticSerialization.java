package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

public class AutomaticSerialization {
	private static Serialization implementation;
	
	private static synchronized Serialization getImplementation() {
		if(implementation == null) {
			ClassLoader cl = AutomaticSerialization.class.getClassLoader();
			try {
				Class<? extends Serialization> ic = 
						Class.forName("org.ssf4j.Implementation", true, cl).
						asSubclass(Serialization.class);
				implementation = ic.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("No implementation of SSF4J found on classpath.", e);
			} catch (InstantiationException e) {
				throw new RuntimeException("Exception instantiating SSF4J serializer", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Unable to access SSF4J serializer constructor", e);
			}
			try {
				Enumeration<URL> impls = cl.getResources("org/ssf4j/Implementation.class");
				URL chosen = impls.nextElement();
				if(impls.hasMoreElements()) {
					System.err.println("Multiple serialization implementations found on classpath");
					System.err.println("Actually chose: " + chosen);
				}
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return implementation;
	}
	
	public static Serializer newSerializer(OutputStream out) throws IOException {
		return getImplementation().newSerializer(out);
	}
	
	public static Deserializer newDeserializer(InputStream in) throws IOException {
		return getImplementation().newDeserializer(in);
	}
}
