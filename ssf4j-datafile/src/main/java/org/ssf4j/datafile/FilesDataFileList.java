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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.ssf4j.Exceptions;
import org.ssf4j.Serialization;

public class FilesDataFileList<T> extends AbstractList<List<T>> implements Closeable {
	protected File cache;
	protected File index;
	
	protected List<DataFileDeserializer<T>> desers;
	
	protected Serialization serde;
	protected Class<T> type;
	
	protected boolean closed;
	
	public FilesDataFileList(File cache, File index, Serialization serde, Class<T> type) throws IOException {
		this.cache = cache;
		this.index = index;
		this.serde = serde;
		this.type = type;

		desers = new ArrayList<DataFileDeserializer<T>>();
		
		if(index.canRead())
			readIndex();
	}
	
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
	
	protected void addIndex(long start, long stop) throws IOException {
		DataOutputStream data = new DataOutputStream(new FileOutputStream(index, true));
		try {
			data.writeLong(start);
			data.writeLong(stop);
		} finally {
			data.close();
		}
	}
	
	public synchronized int append(List<T> list) throws IOException {
		if(closed)
			throw new IllegalStateException(this + " closed");
		
		int index = desers.size();
		
		File tmp = File.createTempFile(cache.getName(), ".tmp");
		DataFile<T> data = new DataFile<T>(tmp, serde, type);
		DataFileSerializer<T> ser = data.newSerializer();
		try {
			for(T e : list)
				ser.write(e);
		} finally {
			ser.close();
		}
		
		long start = cache.length();
		long stop = start + tmp.length();
		
		OutputStream out = new FileOutputStream(cache, true);
		try {
			InputStream in = new FileInputStream(tmp);
			try {
				IOUtils.copy(in, out);
			} finally {
				in.close();
			}
		} finally {
			out.close();
		}
		
		desers.add(new DataFileDeserializer<T>(new FileSeekingInput(cache, start, stop), serde, type));
		addIndex(start, stop);
		
		return index;
	}
	
	@Override
	public synchronized void close() throws IOException {
		closed = true;
		for(DataFileDeserializer<T> des : desers)
			des.close();
	}

	@Override
	public synchronized List<T> get(int index) {
		if(closed)
			throw new IllegalStateException(this + " closed");
		return desers.get(index);
	}

	@Override
	public synchronized int size() {
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
