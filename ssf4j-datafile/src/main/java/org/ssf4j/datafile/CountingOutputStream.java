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
	
	public long getCount() {
		return count;
	}
	
	@Override
	public void flush() throws IOException {
		wrapped.flush();
	}
	
	@Override
	public void close() throws IOException {
		wrapped.close();
	}

}
