package org.ssf4j.datafile.hashfile;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link FilterOutputStream} that counts written bytes
 * @author robin
 *
 */
class CountingOutputStream extends FilterOutputStream {

	/**
	 * The number of bytes written so far
	 */
	private long length;
	
	/**
	 * Create a new {@link CountingOutputStream}, which counts written bytes
	 * @param out The {@link OutputStream} to wrap
	 */
	public CountingOutputStream(OutputStream out) {
		super(out);
		length = 0;
	}
	
	/**
	 * Returns the number of bytes written so far
	 * @return The number of bytes written so far
	 */
	public long getLength() {
		return length;
	}
	
	@Override
	public void write(int b) throws IOException {
		super.write(b);
		length++;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
		length += len;
	}

}
