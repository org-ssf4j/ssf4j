package org.ssf4j.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import org.ssf4j.Locked;
import org.ssf4j.Serialization;
import org.ssf4j.Serialized;

import com.esotericsoftware.kryo.Kryo;

/**
 * Serialization facade that uses Kryo
 * @author robin
 *
 */
public class KryoSerialization implements Serialization, Locked {

	/**
	 * The instance of {@link Kryo} to use for serialization and deserialization.
	 * If {@code null} then a default {@link Kryo} is created for each serializer/deserializer.
	 */
	protected Kryo kryo;
	
	/**
	 * Create a new {@link KryoSerialization} that uses a new default {@link Kryo} for
	 * each new serializer/deserializer
	 */
	public KryoSerialization() {
		this(new Kryo());
		kryo.addDefaultSerializer(Serialized.class, SerializedSerializer.class);
	}
	
	/**
	 * Create a new {@link KryoSerialization} that uses the supplied {@link Kryo} for
	 * serialization/deserialization
	 * @param kryo The Kryo to use
	 */
	public KryoSerialization(Kryo kryo) {
		this.kryo = kryo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> KryoSerializer<T> newSerializer(OutputStream out, Class<T> type) throws IOException {
		return new KryoSerializer<T>(this, kryo, out);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> KryoDeserializer<T> newDeserializer(InputStream in, Class<T> type) throws IOException {
		return new KryoDeserializer<T>(this, kryo, in, type);
	}

	@Override
	public boolean isThreadSafe() {
		return false;
	}

	/**
	 * The lock for this serialization
	 */
	protected ReentrantLock lock = new ReentrantLock();

	@Override
	public ReentrantLock getLock() {
		return lock;
	}

}
