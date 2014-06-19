package org.ssf4j.datafile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializer;

public class DataFile implements Serialization {

	protected File file;
	protected Serialization serde;
	
	public DataFile(File file, Serialization serde) {
		this.file = file;
		this.serde = serde;
	}
	
	@Override
	public <T> Serializer<T> newSerializer(OutputStream out, Class<T> type)
			throws IOException {
		return new DataFileSerializer<T>(new FileOutputStream(file), serde, type);
	}

	@Override
	public <T> Deserializer<T> newDeserializer(InputStream in, Class<T> type)
			throws IOException {
		return new DataFileDeserializer<T>(new RandomAccessFile(file, "r"), serde, type);
	}

}
