package org.ssf4j.datafile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {

	protected ByteBuffer buf;
	
	public ByteBufferInputStream(ByteBuffer buf) {
		this.buf = buf;
	}
	
	@Override
	public int read() throws IOException {
		if(buf.remaining() == 0)
			return -1;
		return 0xff & buf.get();
	}

	@Override
	public int read(byte[] b) throws IOException {
		if(buf.remaining() == 0)
			return -1;
		int len = Math.min(buf.remaining(), b.length);
		buf.get(b, 0, len);
		return len;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if(buf.remaining() == 0)
			return -1;
		len = Math.min(buf.remaining(), len);
		buf.get(b, off, len);
		return len;
	}

}
