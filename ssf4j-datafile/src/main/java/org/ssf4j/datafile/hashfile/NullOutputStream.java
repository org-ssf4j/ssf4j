package org.ssf4j.datafile.hashfile;

import java.io.IOException;
import java.io.OutputStream;

class NullOutputStream extends OutputStream {

	private static NullOutputStream instance = new NullOutputStream();
	
	public static NullOutputStream get() {
		return instance;
	}
	
	@Override
	public void write(int b) throws IOException {
	}
	
	@Override
	public void write(byte[] b) throws IOException {
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
	}

}
