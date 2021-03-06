package org.ssf4j.datafile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.AbstractList;

import org.ssf4j.Deserializer;
import org.ssf4j.Exceptions;
import org.ssf4j.Serialization;

public class DataFileDeserializer<T> extends AbstractList<T> implements Deserializer<T> {
	protected SeekingInput file;
	protected Serialization serde;
	protected Class<T> type;
	
	protected long endPtr;
	protected int size;
	protected int pos;
	protected ByteBuffer obuf = ByteBuffer.wrap(new byte[8]);

	public DataFileDeserializer(SeekingInput file, Serialization serde, Class<T> type) throws IOException {
		this.file = file;
		this.serde = serde;
		this.type = type;
		
		size = loadSize();
	}
	
	public SeekingInput getFile() {
		return file;
	}
	
	protected int loadSize() throws IOException {
		byte[] b = new byte[8];
		file.seek(file.capacity());
		ByteArrayInputStream buf = new ByteArrayInputStream(b);
		DataInputStream dbuf = new DataInputStream(buf);
		
		long pos;
		
		do {
			file.seek(file.position() - 8);
			file.readFully(b);
			buf.reset();
			pos = dbuf.readLong();
			size++;
			file.seek(file.position() - 8);
		} while(pos >= 0);

		endPtr = file.position();
		size--;
		
		return size;
	}

	protected long offset(int pos) throws IOException {
		if(pos == size())
			return endPtr;
		long ptr = file.capacity() - 8L * (size - pos);
		file.seek(ptr);
		file.readFully(obuf.array());
		return obuf.getLong(0);
	}
	
	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public T read() throws IOException {
		return read(pos++);
	}
	
	public T read(int pos) throws IOException {
		if(pos < 0 || pos >= size())
			throw new IndexOutOfBoundsException();
		long start = offset(pos);
		long length = offset(pos+1) - start;
		file.seek(start);
		
		InputStream in = new SeekingInputInputStream(file, length);
		in = new BufferedInputStream(in, 16*1024);
		
		Deserializer<T> de = serde.newDeserializer(in, type);
		try {
			return de.read();
		} finally {
			de.close();
		}
	}
	
	public int size() {
		return size;
	}

	public void seek(int objPos) {
		this.pos = objPos;
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
