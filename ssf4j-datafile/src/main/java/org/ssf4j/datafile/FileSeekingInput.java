package org.ssf4j.datafile;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileSeekingInput implements SeekingInput {
	
	protected RandomAccessFile file;
	protected long start;
	protected long stop;
	
	public FileSeekingInput(File file) throws IOException {
		this(file, 0L, file.length());
	}
	
	public FileSeekingInput(File file, long start, long stop) throws IOException {
		this.file = new RandomAccessFile(file, "r");
		this.start = start;
		this.stop = stop;
		seek(0);
	}
	
	protected long remaining() throws IOException {
		return stop - file.getFilePointer();
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

	@Override
	public long position() throws IOException {
		return file.getFilePointer() - start;
	}

	@Override
	public long capacity() throws IOException {
		return stop - start;
	}

	@Override
	public int read() throws IOException {
		if(file.getFilePointer() >= stop)
			return -1;
		return file.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		len = (int) Math.min(len, remaining());
		return file.read(b, off, len);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		if(read(b, 0, b.length) < b.length)
			throw new EOFException();
	}

	@Override
	public void seek(long pos) throws IOException {
		file.seek(pos + start);
	}

}
