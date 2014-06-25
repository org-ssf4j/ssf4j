package org.ssf4j.datafile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class DataFileSerializer<T> implements Serializer<T> {
	
	protected OutputStream out;
	protected Serialization serde;
	protected Class<T> type;
	
	protected CountingOutputStream cout;
	protected File offsets;
	protected DataOutputStream dbuf;
	
	
	public DataFileSerializer(OutputStream out, Serialization serde, Class<T> type) throws IOException {
		out = new BufferedOutputStream(out, 16*1024);
		
		this.out = out;
		this.serde = serde;
		this.type = type;
		
		cout = new CountingOutputStream(out);
		offsets = File.createTempFile("offsets", ".tmp");
		dbuf = new DataOutputStream(new FileOutputStream(offsets));
		dbuf.writeLong(-1);
	}
	
	@Override
	public void flush() throws IOException {
		cout.flush();
	}

	@Override
	public void close() throws IOException {
		flush();
		dbuf.close();
		InputStream offin = new FileInputStream(offsets);
		byte[] buf = new byte[8192];
		for(int r = offin.read(buf); r != -1; r = offin.read(buf))
			cout.write(buf, 0, r);
		offin.close();
		offsets.delete();
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
