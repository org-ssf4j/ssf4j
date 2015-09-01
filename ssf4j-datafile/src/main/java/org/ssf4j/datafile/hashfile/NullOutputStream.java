package org.ssf4j.datafile.hashfile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link OutputStream} that does nothing
 * @author robin
 *
 */
public class NullOutputStream extends OutputStream {

	/**
	 * Re-used instance
	 */
	private static NullOutputStream instance = new NullOutputStream();
	
	/**
	 * Return a re-usable instance of {@link NullOutputStream}
	 * @return
	 */
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
