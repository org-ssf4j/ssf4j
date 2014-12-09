package org.ssf4j.datafile;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferSeekingInput implements SeekingInput {

	protected ByteBuffer buffer;
	protected int start;
	protected int stop;
	protected boolean closed;
	
	public ByteBufferSeekingInput(ByteBuffer buffer) {
		this.buffer = buffer;
		start = buffer.position();
		stop = buffer.limit();
	}
	
	protected void checkOpen() throws IOException {
		if(closed)
			throw new IOException(this + " closed");
	}
	
	@Override
	public void close() throws IOException {
		closed = true;
	}

	@Override
	public long position() throws IOException {
		checkOpen();
		return buffer.position() - start;
	}

	@Override
	public long capacity() throws IOException {
		checkOpen();
		return stop - start;
	}

	@Override
	public int read() throws IOException {
		checkOpen();
		if(buffer.remaining() == 0)
			return -1;
		return 0xff & buffer.get();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkOpen();
		int count = Math.min(len, buffer.remaining());
		if(buffer.remaining() == 0 && len > 0)
			return -1;
		buffer.get(b, off, count);
		return count;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		checkOpen();
		if(read(b, 0, b.length) < b.length)
			throw new EOFException();
	}

	@Override
	public void seek(long pos) throws IOException {
		checkOpen();
		buffer.position(start + (int) pos);
	}

}
