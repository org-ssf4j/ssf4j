package org.ssf4j.jdk;

import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectOutput;

import org.ssf4j.ObjectDataOutput;
import org.ssf4j.Serialized;

public class JdkObjectDataOutput implements ObjectDataOutput {
	public static void write(Serialized thiz, ObjectOutput out) throws IOException {
		thiz.write(new JdkObjectDataOutput(out));
	}
	
	protected ObjectOutput out;
	
	public JdkObjectDataOutput(ObjectOutput out) {
		this.out = out;
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void writeBoolean(boolean v) throws IOException {
		out.writeBoolean(v);
	}

	public void writeByte(int v) throws IOException {
		out.writeByte(v);
	}

	public void writeShort(int v) throws IOException {
		out.writeShort(v);
	}

	public void writeChar(int v) throws IOException {
		out.writeChar(v);
	}

	public void writeInt(int v) throws IOException {
		out.writeInt(v);
	}

	public void writeLong(long v) throws IOException {
		out.writeLong(v);
	}

	public void writeFloat(float v) throws IOException {
		out.writeFloat(v);
	}

	public void writeDouble(double v) throws IOException {
		out.writeDouble(v);
	}

	public void writeBytes(String s) throws IOException {
		out.writeBytes(s);
	}

	public void writeChars(String s) throws IOException {
		out.writeChars(s);
	}

	public void writeUTF(String s) throws IOException {
		out.writeUTF(s);
	}

	@Override
	public void write(Object object) throws IOException {
		write(false, object);
	}

	@Override
	public void write(boolean nulls, Object object) throws IOException {
		write(nulls, false, object);
	}

	@Override
	public void write(boolean nulls, boolean polymorphic, Object object)
			throws IOException {
		out.writeObject(object);
	}
}
