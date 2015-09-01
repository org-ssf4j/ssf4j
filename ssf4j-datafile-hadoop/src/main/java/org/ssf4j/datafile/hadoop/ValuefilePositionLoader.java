package org.ssf4j.datafile.hadoop;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.SeekingInput;
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
	
	public V load(ValuefilePosition hp) throws IOException {
		Path path = new Path(hp.getPath().toString());
		
		SeekingInput in = new PathSeekingInput(path, conf);
		
		HashFileReader<?, V> reader = new HashFileReader<Object, V>(MessageDigestUtil.SHA1, serde, Object.class, valueType, (List<HashPosition>) null, in);
		V value = reader.getByPosition(hp.getOffset());
		reader.close();
		
		return value;
	}
}
