package org.ssf4j.datafile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ssf4j.Deserializer;
import org.ssf4j.Exceptions;
import org.ssf4j.Serialization;

public class MappedDataFileDeserializer<T> extends AbstractList<T> implements Deserializer<T> {
	protected MappedByteBuffer mbb;
	protected Serialization serde;
	protected Class<T> type;
	
	protected List<Integer> offsets;
	protected int objPos;

	public MappedDataFileDeserializer(RandomAccessFile file, Serialization serde, Class<T> type) throws IOException {
		this.mbb = file.getChannel().map(MapMode.READ_ONLY, 0, file.length());
		this.serde = serde;
		this.type = type;
		
		offsets = loadOffsets();
	}
	
	protected List<Integer> loadOffsets() throws IOException {
		List<Integer> ret = new ArrayList<Integer>();
		
		mbb.position(mbb.capacity());
		
		int pos;
		
		do {
			mbb.position(mbb.position() - 8);
			ret.add(pos = (int) mbb.getLong());
			mbb.position(mbb.position() - 8);
		} while(pos >= 0);

		ret.remove(ret.size()-1);
		Collections.reverse(ret);
		ret.add(mbb.position());
		return ret;
	}
	
	public void load() {
		mbb.load();
	}

	@Override
	public void close() throws IOException {
		mbb = null;
		System.gc();
	}

	@Override
	public T read() throws IOException {
		return read(objPos++);
	}
	
	public T read(int pos) throws IOException {
		if(pos < 0 || pos >= size())
			throw new IndexOutOfBoundsException();
		int start = offsets.get(pos);
		int stop = offsets.get(pos+1);
		mbb.clear().position(start).limit(stop);
		
		Deserializer<T> de = serde.newDeserializer(new ByteBufferInputStream(mbb), type);
		return de.read();
	}
	
	public int size() {
		return offsets.size() - 1;
	}

	public void seek(int objPos) {
		this.objPos = objPos;
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
