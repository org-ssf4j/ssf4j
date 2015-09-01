package org.ssf4j.datafile.hashfile;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.ssf4j.datafile.SeekingInput;

/**
 * A {@link List} of {@link HashPosition}, backed by a {@link SeekingInput}
 * @author robin
 *
 */
public class HashPositionList extends AbstractList<HashPosition> implements RandomAccess, Closeable {
	
	/**
	 * Copy, sort, and write the argument {@link HashPosition}s to the output stream
	 * @param hashes The {@link HashPosition}s to write
	 * @param out The output stream
	 * @throws IOException
	 */
	public static void write(List<HashPosition> hashes, OutputStream out) throws IOException {
		hashes = new ArrayList<HashPosition>(hashes);
		Collections.sort(hashes);
		write(hashes.iterator(), out);
	}
	
	/**
	 * Write pre-sorted {@link HashPosition}s to the output stream
	 * @param hashes The {@link HashPosition}s to write
	 * @param out The output stream
	 * @throws IOException
	 */
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
	
	/**
	 * The input backing this {@link List}
	 */
	private SeekingInput input;
	/**
	 * The persisted size of a {@link HashPosition}
	 */
	private int hashPersistedSize;
	
	/**
	 * Create a {@link HashPositionList} from a {@link SeekingInput} for a given {@link MessageDigestUtil}
	 * @param input The backing {@link SeekingInput}
	 * @param mdu The source of message digests
	 */
	public HashPositionList(SeekingInput input, MessageDigestUtil mdu) {
		this(input, HashPosition.persistedSize(mdu));
	}
	
	/**
	 * Create a {@link HashPositionList} from a {@link SeekingInput} for a given size of {@link HashPosition}
	 * @param input The backing {@link SeekingInput}
	 * @param hashPersistedSize The size of a persisted {@link HashPosition}
	 */
	public HashPositionList(SeekingInput input, int hashPersistedSize) {
		this.input = input;
		this.hashPersistedSize = hashPersistedSize;
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
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
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

	@Override
	public void close() throws IOException {
		input.close();
	}
	
	
}
