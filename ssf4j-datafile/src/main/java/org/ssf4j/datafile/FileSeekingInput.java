package org.ssf4j.datafile;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {@link SeekingInput} that reads from a {@link File}
 * @author robin
 *
 */
public class FileSeekingInput implements SeekingInput {
	
	/**
	 * {@link RandomAccessFile} to access the file
	 */
	protected RandomAccessFile file;
	/**
	 * The start position within the {@link RandomAccessFile}
	 */
	protected long start;
	/**
	 * The stop position within the {@link RandomAccessFile}
	 */
	protected long stop;
	
	/**
	 * Create a new {@link FileSeekingInput} over the entire file
	 * @param file
	 * @throws IOException
	 */
	public FileSeekingInput(File file) throws IOException {
		this(file, 0L, file.length());
	}
	
	/**
	 * Create a new {@link FileSeekingInput} over a range in the file
	 * @param file
	 * @param start
	 * @param stop
	 * @throws IOException
	 */
	public FileSeekingInput(File file, long start, long stop) throws IOException {
		this.file = new RandomAccessFile(file, "r");
		this.start = start;
		this.stop = stop;
		seek(0);
	}
	
	public RandomAccessFile getFile() {
		return file;
	}
	
	public long getStart() {
		return start;
	}
	
	public long getStop() {
		return stop;
	}
	
	/**
	 * Return the number of remaining bytes
	 * @return
	 * @throws IOException
	 */
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
