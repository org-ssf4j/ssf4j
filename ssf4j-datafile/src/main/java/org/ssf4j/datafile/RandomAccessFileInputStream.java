package org.ssf4j.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream extends InputStream {

	protected RandomAccessFile file;
	protected long length;
	
	public RandomAccessFileInputStream(RandomAccessFile file, long length) {
		this.file = file;
		this.length = length;
	}
	
	@Override
	public int read() throws IOException {
		if(length == 0)
			return -1;
		length--;
		return file.read();
		
	}

	@Override
	public int read(byte[] b) throws IOException {
		if(length == 0)
			return -1;
		int r = file.read(b, 0, Math.min(b.length, (int) length));
		if(r > 0)
			length -= r;
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(length == 0)
			return -1;
		int r = file.read(b, off, Math.min(len, (int) length));
		if(r > 0)
			length -= r;
		return r;
	}

}
