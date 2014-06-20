package org.ssf4j.datafile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class DataFileSerializer<T> implements Serializer<T> {
	
	protected CountingOutputStream out;
	protected ByteArrayOutputStream buf;
	protected DataOutputStream dbuf;
	protected Serializer<T> ser;
	
	public DataFileSerializer(OutputStream out, Serialization serde, Class<T> type) throws IOException {
		this.out = new CountingOutputStream(out);
		
		buf = new ByteArrayOutputStream();
		dbuf = new DataOutputStream(buf);
		ser = serde.newSerializer(this.out, type);
		
		ser.flush();
		dbuf.writeLong(-1 - this.out.getCount());
	}
	
	@Override
	public void flush() throws IOException {
		ser.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		dbuf.flush();
		out.write(buf.toByteArray());
		ser.close();
	}

	@Override
	public void write(T object) throws IOException {
		flush();
		dbuf.writeLong(out.getCount());
		ser.write(object);
	}

}
