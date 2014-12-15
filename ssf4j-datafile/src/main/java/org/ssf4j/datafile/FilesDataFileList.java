package org.ssf4j.datafile;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ssf4j.Exceptions;
import org.ssf4j.Serialization;

/**
 * List of {@link DataFileDeserializer}s.  Basically a way to keep a lot of {@link DataFile}s in
 * a single file.  Requires two files, one for the data, and one for the index.
 * @author robin
 *
 * @param <T>
 */
public class FilesDataFileList<T> extends AbstractList<List<T>> implements Closeable {
	/**
	 * File that holds the data
	 */
	protected File cache;
	/**
	 * File that holds the indexes of the data
	 */
	protected File index;

	/**
	 * The {@link DataFileDeserializer}s already in the list
	 */
	protected List<DataFileDeserializer<T>> desers;

	/**
	 * The {@link Serialization} to use
	 */
	protected Serialization serde;
	/**
	 * The class of object being stored
	 */
	protected Class<T> type;

	/**
	 * Whether this list has been closed
	 */
	protected boolean closed;

	/**
	 * Create a new {@link FilesDataFileList}
	 * @param cache
	 * @param index
	 * @param serde
	 * @param type
	 * @throws IOException
	 */
	public FilesDataFileList(File cache, File index, Serialization serde, Class<T> type) throws IOException {
		this.cache = cache;
		this.index = index;
		this.serde = serde;
		this.type = type;

		desers = new CopyOnWriteArrayList<DataFileDeserializer<T>>();

		if(index.canRead())
			readIndex();
	}

	/**
	 * Read an existing index file and create deserializers for it
	 * @throws IOException
	 */
	protected void readIndex() throws IOException {
		DataInputStream data = new DataInputStream(new FileInputStream(index));
		try {
			try {
				for(;;) {
					long start = data.readLong();
					long stop = data.readLong();
					desers.add(new DataFileDeserializer<T>(new FileSeekingInput(cache, start, stop), serde, type));
				}
			} catch(EOFException e) {
			}
		} finally {
			data.close();
		}
	}

	/**
	 * Add an entry to the index, creating if necessary
	 * @param start
	 * @param stop
	 * @throws IOException
	 */
	protected void addIndex(long start, long stop) throws IOException {
		DataOutputStream data = new DataOutputStream(new FileOutputStream(index, true));
		try {
			data.writeLong(start);
			data.writeLong(stop);
		} finally {
			data.close();
		}
	}

	protected Object[] createDataFile(List<T> list) throws IOException {
		final File tmp = new File(System.getProperty("java.io.tmpdir"), cache.getName() + "." + UUID.randomUUID() + ".tmp");
		DataFile<T> data = new DataFile<T>(tmp, serde, type);
		DataFileSerializer<T> ser = data.newSerializer();
		try {
			for(T e : list)
				ser.write(e);
		} finally {
			ser.close();
		}
		InputStream in = new FileInputStream(tmp) {
			@Override
			public void close() throws IOException {
				super.close();
				tmp.delete();
			}
		};
		return new Object[] {in, tmp.length()};
	}
	
	/**
	 * Append another list of data to this list
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public int append(List<T> list) throws IOException {
		if(closed)
			throw new IllegalStateException(this + " closed");


		Object[] inlen = createDataFile(list);

		synchronized(this) {
			int index = desers.size();
			long start = cache.length();
			long stop = start + (Long) inlen[1];

			InputStream in = (InputStream) inlen[0];
			try {
				OutputStream out = new FileOutputStream(cache, true);
				try {
					byte[] buf = new byte[8192];
					for(int r = in.read(buf); r != -1; r = in.read(buf))
						out.write(buf, 0, r);
				} finally {
					out.close();
				}
			} finally {
				in.close();
			}

			desers.add(new DataFileDeserializer<T>(new FileSeekingInput(cache, start, stop), serde, type));
			addIndex(start, stop);

			return index;
		}
	}

	@Override
	public void close() throws IOException {
		closed = true;
		for(DataFileDeserializer<T> des : desers)
			des.close();
	}

	@Override
	public List<T> get(int index) {
		if(closed)
			throw new IllegalStateException(this + " closed");
		return desers.get(index);
	}

	@Override
	public int size() {
		if(closed)
			throw new IllegalStateException(this + " closed");
		return desers.size();
	}

	@Override
	public boolean add(List<T> e) {
		try {
			append(e);
			return true;
		} catch(IOException ex) {
			throw Exceptions.runtime(ex);
		}
	}
}
