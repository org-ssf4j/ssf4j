package org.ssf4j.datafile;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {
	protected OutputStream wrapped;
	protected long count;
	
	public CountingOutputStream(OutputStream wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public void write(int b) throws IOException {
		wrapped.write(b);
		count++;
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		wrapped.write(b);
		count += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		wrapped.write(b, off, len);
		count += len;
	}

	@Override
	public void flush() throws IOException {
		wrapped.flush();
	}
	
	/**
	 * Doesn't close the underlying stream!
	 */
	@Override
	public void close() throws IOException {
		// NOP
	}

	public long getCount() {
		return count;
	}

}
