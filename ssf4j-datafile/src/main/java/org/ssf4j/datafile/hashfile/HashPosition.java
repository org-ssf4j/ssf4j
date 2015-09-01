package org.ssf4j.datafile.hashfile;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A simple pair of a {@code byte[]} hash and a file offset
 * @author robin
 *
 */
public class HashPosition implements Comparable<HashPosition> {
	/**
	 * Returns the length of a persisted {@link HashPosition} for a given {@link MessageDigestUtil}
	 * @param mdu The source of digests
	 * @return The size of a persisted {@link HashPosition}
	 */
	public static int persistedSize(MessageDigestUtil mdu) {
		return mdu.getLength() + PERSISTED_POSITION_SIZE;
	}
	
	/**
	 * {@link Comparator} for {@link HashPosition}.  Compares based on byte order
	 * of the hash.
	 */
	public static final Comparator<HashPosition> HASH_POSITION_ORDER = new Comparator<HashPosition>() {
		@Override
		public int compare(HashPosition o1, HashPosition o2) {
			return ByteArrays.BYTE_ARRAY_ORDER.compare(o1.getHash(), o2.getHash());
		}
	};
	
	/**
	 * The peristed size of the position
	 */
	public static final int PERSISTED_POSITION_SIZE = ByteArrays.LENGTH_LONG;
	
	/**
	 * The hash
	 */
	private byte[] hash;
	/**
	 * The position
	 */
	private long position;
	
	/**
	 * The bytes to be persisted
	 */
	private transient byte[] persisted;
	
	/**
	 * Create a {@link HashPosition} from a persisted {@code byte[]}
	 * @param persisted The persisted {@code byte[]}, as returned by {@link #getPersisted()}
	 */
	public HashPosition(byte[] persisted) {
		if(persisted == null)
			throw new NullPointerException();
		this.persisted = persisted;
		hash = Arrays.copyOf(persisted, persisted.length - PERSISTED_POSITION_SIZE);
		position = ByteArrays.toLong(persisted, persisted.length - PERSISTED_POSITION_SIZE);
	}
	
	/**
	 * Create a {@link HashPosition} for a hash and file position
	 * @param hash The has
	 * @param position The file position
	 */
	public HashPosition(byte[] hash, long position) {
		if(hash == null)
			throw new NullPointerException();
		this.hash = hash;
		this.position = position;
	}
	
	/**
	 * Returns the hash
	 * @return The hash
	 */
	public byte[] getHash() {
		return hash;
	}
	
	/**
	 * Returns the position
	 * @return The position
	 */
	public long getPosition() {
		return position;
	}
	
	/**
	 * Returns the bytes to be persisted
	 * @return The bytes to be persisted
	 */
	public byte[] getPersisted() {
		if(persisted == null) {
			byte[] b = Arrays.copyOf(hash, hash.length + PERSISTED_POSITION_SIZE);
			ByteArrays.toBytes(b, hash.length, position);
			persisted = b;
		}
		return persisted;
	}

	@Override
	public int compareTo(HashPosition o) {
		return HASH_POSITION_ORDER.compare(this, o);
	}
}
