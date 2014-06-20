package org.ssf4j.datafile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ssf4j.Deserializer;
import org.ssf4j.Exceptions;
import org.ssf4j.Serialization;

public class DataFileDeserializer<T> extends AbstractList<T> implements Deserializer<T> {
	protected RandomAccessFile file;
	protected List<Long> offsets;
	protected int objPos;
	
	protected Deserializer<T> de;
	
	public DataFileDeserializer(RandomAccessFile file, Serialization serde, Class<T> type) throws IOException {
		this.file = file;
		
		offsets = loadOffsets();
		
		file.seek(0);
		
		de = serde.newDeserializer(new RandomAccessFileInputStream(file), type);
	}
	
	protected List<Long> loadOffsets() throws IOException {
		List<Long> ret = new ArrayList<Long>();
		
		byte[] b = new byte[8];
		file.seek(file.length());
		ByteArrayInputStream buf = new ByteArrayInputStream(b);
		DataInputStream dbuf = new DataInputStream(buf);
		
		long pos;
		
		do {
			file.seek(file.getFilePointer() - 8);
			file.readFully(b);
			buf.reset();
			ret.add(pos = dbuf.readLong());
			file.seek(file.getFilePointer() - 8);
		} while(pos >= 0);
		
		ret.remove(ret.size() - 1); // remove trailing negative offset
		Collections.reverse(ret);
		return ret;
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public T read() throws IOException {
		return read(objPos++);
	}
	
	public T read(int pos) throws IOException {
		file.seek(offsets.get(pos));
		return de.read();
	}
	
	public int size() {
		return offsets.size();
	}

	@Override
	public T get(int index) {
		try {
			return read(index);
		} catch(Exception e) {
			throw Exceptions.runtime(e);
		}
	}
}
