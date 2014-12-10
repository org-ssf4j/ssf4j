package org.ssf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.locks.ReentrantLock;

public class AutomaticSerialization implements Serialization {
	private static AutomaticSerialization instance;
	private static Serialization implementation;
	
	public synchronized static AutomaticSerialization get() {
		if(instance == null)
			instance = createAutomatic();
		return instance;
	}
	
	private static AutomaticSerialization createAutomatic() {
		if(getImplementation() instanceof Locked)
			return new LockedAutomaticSerialization();
		else
			return new AutomaticSerialization();
	}
	
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
					System.err.println("Multiple serialization implementations found on classpath. Actually chose: " + chosen);
				}
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
		return implementation;
	}
	
	private AutomaticSerialization() {
	}
	
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException {
		if(this instanceof Locked)
			((Locked) this).getLock().lock();
		try {
			return getImplementation().newSerializer(out, type);
		} finally {
			if(this instanceof Locked)
				((Locked) this).getLock().unlock();
		}
	}
	
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException {
		if(this instanceof Locked)
			((Locked) this).getLock().lock();
		try {
			return getImplementation().newDeserializer(in, type);
		} finally {
			if(this instanceof Locked)
				((Locked) this).getLock().unlock();
		}
	}

	@Override
	public boolean isThreadSafe() {
		return getImplementation().isThreadSafe();
	}
	
	private static class LockedAutomaticSerialization extends AutomaticSerialization implements Locked {
		private LockedAutomaticSerialization() {
		}

		@Override
		public ReentrantLock getLock() {
			return ((Locked) getImplementation()).getLock();
		}
	}
}
