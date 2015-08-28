package org.ssf4j.datafile.hadoop;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.SeekingInput;
import org.ssf4j.datafile.hashfile.ByteArrays;
import org.ssf4j.datafile.hashfile.HashFileReader;
import org.ssf4j.datafile.hashfile.HashPosition;
import org.ssf4j.datafile.hashfile.MessageDigestUtil;

public class ValuefilePositionLoader<V> {
	protected Class<V> valueType;
	protected Serialization serde;
	protected Configuration conf;
	
	public ValuefilePositionLoader(Class<V> valueType, Configuration conf) {
		this(valueType, conf, Serializations.get(Serializations.AVRO_BINARY));
	}
	
	public ValuefilePositionLoader(Class<V> valueType, Configuration conf, Serialization serde) {
		this.valueType = valueType;
		this.serde = serde;
		this.conf = conf;
	}
	
	public V load(ValuefilePosition vp) throws IOException {
		Path path = new Path(vp.getPath().toString());
		
		InputStream in = path.getFileSystem(conf).open(path);
		in = new GZIPInputStream(in);

		in.skip(vp.getOffset() + ByteArrays.LENGTH_LONG);

		Deserializer<V> des = serde.newDeserializer(in, valueType);
		V value = des.read();
		des.close();
		
		return value;
	}
}
