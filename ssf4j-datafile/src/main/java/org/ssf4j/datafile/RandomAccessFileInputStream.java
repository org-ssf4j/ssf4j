package org.ssf4j.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream extends InputStream {

	protected RandomAccessFile file;
	
	public RandomAccessFileInputStream(RandomAccessFile file) {
		this.file = file;
	}
	
	@Override
	public int read() throws IOException {
		return file.read();
	}

}
