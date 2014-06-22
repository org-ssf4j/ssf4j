package org.ssf4j.datafile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class DataFileSerializer<T> implements Serializer<T> {
	
	protected OutputStream out;
	protected Serialization serde;
	protected Class<T> type;
	
	protected CountingOutputStream cout;
	protected ByteArrayOutputStream buf;
	protected DataOutputStream dbuf;
	
	
	public DataFileSerializer(OutputStream out, Serialization serde, Class<T> type) throws IOException {
		out = new BufferedOutputStream(out, 16*1024);
		
		this.out = out;
		this.serde = serde;
		this.type = type;
		
		cout = new CountingOutputStream(out);
		buf = new ByteArrayOutputStream();
		dbuf = new DataOutputStream(buf);
		dbuf.writeLong(-1 - this.cout.getCount());
	}
	
	@Override
	public void flush() throws IOException {
		cout.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		dbuf.flush();
		cout.write(buf.toByteArray());
		cout.flush();
		out.close();
	}

	@Override
	public void write(T object) throws IOException {
		flush();
		dbuf.writeLong(cout.getCount());
		Serializer<T> ser = serde.newSerializer(this.cout, type);
		ser.write(object);
		ser.close();
	}

}
