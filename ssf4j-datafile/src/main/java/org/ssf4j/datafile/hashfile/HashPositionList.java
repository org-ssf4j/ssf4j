package org.ssf4j.datafile.hashfile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.ssf4j.datafile.SeekingInput;

public class HashPositionList extends AbstractList<HashPosition> implements RandomAccess {
	public static void write(List<HashPosition> hashes, OutputStream out) throws IOException {
		hashes = new ArrayList<HashPosition>(hashes);
		Collections.sort(hashes);
		write(hashes.iterator(), out);
	}
	
	public static void write(Iterator<HashPosition> hashes, OutputStream out) throws IOException {
		HashPosition prev = null;
		while(hashes.hasNext()) {
			HashPosition next = hashes.next();
			out.write(next.getPersisted());
			if(prev != null && HashPosition.HASH_POSITION_ORDER.compare(prev, next) >= 0)
				throw new IllegalArgumentException("HashPositions out of order");
			prev = next;
		}
	}
	
	private SeekingInput input;
	private int hashPersistedSize;
	
	public HashPositionList(SeekingInput input, MessageDigestUtil mdu) {
		this(input, HashPosition.persistedSize(mdu));
	}
	
	public HashPositionList(SeekingInput input, int hashPersistedSize) {
		this.input = input;
		this.hashPersistedSize = hashPersistedSize;
	}

	public int indexOfHash(byte[] hash) {
		return indexOf(new HashPosition(hash, 0));
	}
	
	@Override
	public int indexOf(Object o) {
		if(o instanceof HashPosition) {
			int i = Collections.binarySearch(this, (HashPosition) o);
			return Math.max(-1, i);
		}
		return -1;
	}
	
	@Override
	public HashPosition get(int index) {
		try {
			byte[] b = new byte[hashPersistedSize];
			input.seek(index + hashPersistedSize);
			input.readFully(b);
			return new HashPosition(b);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int size() {
		try {
			return (int)(input.capacity() / hashPersistedSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
