package org.ssf4j.jdk;

import java.io.DataInput;
import java.io.IOException;
import java.io.ObjectInput;

import org.ssf4j.ObjectDataInput;
import org.ssf4j.Serialized;

public class JdkObjectDataInput implements ObjectDataInput {
	public static void read(Serialized thiz, ObjectInput in) throws IOException {
		thiz.read(new JdkObjectDataInput(in));
	}
	
	protected ObjectInput in;
	
	public JdkObjectDataInput(ObjectInput in) {
		this.in = in;
	}

	public void readFully(byte[] b) throws IOException {
		in.readFully(b);
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		in.readFully(b, off, len);
	}

	public int skipBytes(int n) throws IOException {
		return in.skipBytes(n);
	}

	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

	public byte readByte() throws IOException {
		return in.readByte();
	}

	public int readUnsignedByte() throws IOException {
		return in.readUnsignedByte();
	}

	public short readShort() throws IOException {
		return in.readShort();
	}

	public int readUnsignedShort() throws IOException {
		return in.readUnsignedShort();
	}

	public char readChar() throws IOException {
		return in.readChar();
	}

	public int readInt() throws IOException {
		return in.readInt();
	}

	public long readLong() throws IOException {
		return in.readLong();
	}

	public float readFloat() throws IOException {
		return in.readFloat();
	}

	public double readDouble() throws IOException {
		return in.readDouble();
	}

	public String readLine() throws IOException {
		return in.readLine();
	}

	public String readUTF() throws IOException {
		return in.readUTF();
	}

	@Override
	public <T> T read(Class<T> type) throws IOException {
		return read(false, type);
	}

	@Override
	public <T> T read(boolean nulls, Class<T> type) throws IOException {
		return read(nulls, false, type);
	}

	@Override
	public <T> T read(boolean nulls, boolean polymorphic, Class<T> type)
			throws IOException {
		try {
			return type.cast(in.readObject());
		} catch(ClassNotFoundException e) {
			throw new IOException(e);
		}
	}
}
