package org.ssf4j.datafile;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * A source of input bytes that can be seeked.  Such a source might be implemented
 * by a wrapper around a {@link RandomAccessFile}, or perhaps a {@link ByteBuffer}.
 * @author robin
 *
 */
public interface SeekingInput extends Closeable {
	/**
	 * Close the input
	 * @throws IOException
	 */
	public void close() throws IOException;
	/**
	 * Return the input's position
	 * @return
	 * @throws IOException
	 */
	public long position() throws IOException;
	/**
	 * Return the input's capacity
	 * @return
	 * @throws IOException
	 */
	public long capacity() throws IOException;
	/**
	 * Read one byte from the current position
	 * @return
	 * @throws IOException
	 * @see {@link InputStream#read()}
	 */
	public int read() throws IOException;
	/**
	 * Read a byte array from the current position
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 * @throws IOException
	 * @see {@link InputStream#read(byte[], int, int)}
	 */
	public int read(byte[] b, int off, int len) throws IOException;
	/**
	 * Fully read a byte array from the current position
	 * @param b
	 * @throws IOException
	 * @see {@link DataInput#readFully(byte[])}
	 */
	public void readFully(byte[] b) throws IOException;
	/**
	 * Change the current position
	 * @param pos
	 * @throws IOException
	 * @see {@link RandomAccessFile#seek(long)}
	 */
	public void seek(long pos) throws IOException;
}
