package org.ssf4j.datafile.hashfile;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class CountingOutputStream extends FilterOutputStream {

	private long length;
	
	public CountingOutputStream(OutputStream out) {
		super(out);
		length = 0;
	}
	
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
