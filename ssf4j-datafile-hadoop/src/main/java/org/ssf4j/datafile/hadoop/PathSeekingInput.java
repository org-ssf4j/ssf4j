package org.ssf4j.datafile.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.ssf4j.datafile.SeekingInput;

/**
 * {@link SeekingInput} backed by a {@link Path} as accessed by a {@link FileSystem}
 * @author robin
 *
 */
public class PathSeekingInput implements SeekingInput {
	
	/**
	 * The wrapped stream
	 */
	private FSDataInputStream in;
	/**
	 * The size of the path file
	 */
	private long capacity;
	
	/**
	 * Create a new {@link PathSeekingInput} using a new (default) {@link Configuration}
	 * @param path The {@link Path} of the input file
	 * @throws IOException
	 */
	public PathSeekingInput(Path path) throws IOException {
		this(path, new Configuration());
	}
	
	/**
	 * Create a new {@link PathSeekingInput} using a specified {@link Configuration} to load
	 * the {@link FileSystem}
	 * @param path The {@link Path} of the input file
	 * @param conf The {@link Configuration} to use to get the {@link FileSystem}
	 * @throws IOException
	 */
	public PathSeekingInput(Path path, Configuration conf) throws IOException {
		this(path, path.getFileSystem(conf));
	}
	
	/**
	 * Create a new {@link PathSeekingInput} using a specified {@link Path} and {@link FileSystem}
	 * @param path The {@link Path} of the input file
	 * @param fs The {@link FileSystem} of the input file
	 * @throws IOException
	 */
	public PathSeekingInput(Path path, FileSystem fs) throws IOException {
		this(fs.open(path), fs.getFileStatus(path).getLen());
	}

	/**
	 * Create a {@link PathSeekingInput} wrapping a {@link FSDataInputStream} with
	 * pre-loaded capacity
	 * @param in The stream to wrap
	 * @param capacity The capacity
	 */
	private PathSeekingInput(FSDataInputStream in, long capacity) {
		this.in = in;
		this.capacity = capacity;
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public long position() throws IOException {
		return in.getPos();
	}

	@Override
	public long capacity() throws IOException {
		return capacity;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		in.readFully(b);
	}

	@Override
	public void seek(long pos) throws IOException {
		in.seek(pos);
	}

}
