package org.ssf4j.datafile.hashfile;

import java.util.Arrays;
import java.util.Comparator;

public class HashPosition implements Comparable<HashPosition> {
	public static int persistedSize(MessageDigestUtil mdu) {
		return mdu.getLength() + POSITION_SIZE;
	}
	
	public static final Comparator<HashPosition> HASH_POSITION_ORDER = new Comparator<HashPosition>() {
		@Override
		public int compare(HashPosition o1, HashPosition o2) {
			return ByteArrays.BYTE_ARRAY_ORDER.compare(o1.getHash(), o2.getHash());
		}
	};
	
	public static final int POSITION_SIZE = 8;
	
	private byte[] hash;
	private long position;
	
	private transient byte[] persisted;
	
	public HashPosition(byte[] persisted) {
		if(persisted == null)
			throw new NullPointerException();
		this.persisted = persisted;
		hash = Arrays.copyOf(persisted, persisted.length - POSITION_SIZE);
		position = ByteArrays.toLong(persisted, persisted.length - POSITION_SIZE);
	}
	
	public HashPosition(byte[] hash, long position) {
		if(hash == null)
			throw new NullPointerException();
		this.hash = hash;
		this.position = position;
	}
	
	public byte[] getHash() {
		return hash;
	}
	
	public long getPosition() {
		return position;
	}
	
	public byte[] getPersisted() {
		if(persisted == null) {
			byte[] b = Arrays.copyOf(hash, hash.length + POSITION_SIZE);
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
