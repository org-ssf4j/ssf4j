package org.ssf4j.datafile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ssf4j.Serialization;

public class DataFile<T> {

	protected File file;
	protected Serialization serde;
	protected Class<T> type;
	
	public DataFile(File file, Serialization serde, Class<T> type) {
		this.file = file;
		this.serde = serde;
		this.type = type;
	}
	
	public DataFileSerializer<T> newSerializer()
			throws IOException {
		return new DataFileSerializer<T>(new FileOutputStream(file), serde, type);
	}

	public DataFileDeserializer<T> newDeserializer()
			throws IOException {
		return new DataFileDeserializer<T>(new FileSeekingInput(file), serde, type);
	}
	
}
