package org.ssf4j.datafile.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.SeekingInput;
import org.ssf4j.datafile.hashfile.HashFileReader;
import org.ssf4j.datafile.hashfile.MessageDigestUtil;

public class HashfilePositionLoader<V> {
	protected Class<V> valueType;
	protected Serialization serde;
	protected Configuration conf;
	
	public HashfilePositionLoader(Class<V> valueType, Configuration conf) {
		this(valueType, conf, Serializations.get(Serializations.AVRO_BINARY));
	}
	
	public HashfilePositionLoader(Class<V> valueType, Configuration conf, Serialization serde) {
		this.valueType = valueType;
		this.serde = serde;
		this.conf = conf;
	}
	
	public V load(HashfilePosition hp) throws IOException {
		Path keysPath = new Path(hp.getKeysPath().toString());
		Path valuesPath = new Path(hp.getValuesPath().toString());
		
		SeekingInput keysIn = new PathSeekingInput(keysPath, conf);
		SeekingInput valuesIn = new PathSeekingInput(valuesPath, conf);
		
		HashFileReader<?, V> reader = new HashFileReader<Object, V>(MessageDigestUtil.SHA1, serde, Object.class, valueType, keysIn, valuesIn);
		V value = reader.getByHash(hp.getKeyHash().bytes());
		reader.close();
		
		return value;
	}
}
