package org.ssf4j.datafile.mapreduce;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.ssf4j.Deserializer;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.hadoop.ValuefilePosition;
import org.ssf4j.datafile.hashfile.ByteArrays;

public class ValuefileInputFormat<V> extends FileInputFormat<ValuefilePosition, V> {
	private static final String PREFIX = ValuefileInputFormat.class.getName();
	
	public static final String SERIALIZATION_CLASS_KEY = PREFIX + ".serialization_class";
	public static final String VALUE_TYPE_KEY = PREFIX + ".value_type";

	public static final String DEFAULT_SERIALIZATION_CLASS = Serializations.AVRO_BINARY;

	public static void setSerializationClassName(Job job, String serializationClassName) {
		job.getConfiguration().set(SERIALIZATION_CLASS_KEY, serializationClassName);
	}
	
	public static void setSerializationClass(Job job, Class<? extends Serialization> serializationClass) {
		if(serializationClass.isInterface())
			throw new IllegalArgumentException();
		job.getConfiguration().setClass(SERIALIZATION_CLASS_KEY, serializationClass, Serialization.class);
	}
	
	public static void setValueType(Job job, Class<?> valueType) {
		job.getConfiguration().set(VALUE_TYPE_KEY, valueType.getName());
	}
	
	protected static String getSerializationClassName(Configuration c) {
		return c.get(SERIALIZATION_CLASS_KEY, DEFAULT_SERIALIZATION_CLASS);
	}
	
	protected static Class<? extends Serialization> getSerializationClass(Configuration c) {
		return c.getClass(SERIALIZATION_CLASS_KEY, c.getClassByNameOrNull(DEFAULT_SERIALIZATION_CLASS).asSubclass(Serialization.class), Serialization.class);
	}
	
	protected static Class<?> getValueType(Configuration c) {
		return c.getClass(VALUE_TYPE_KEY, null);
	}
	
	@Override
	public RecordReader<ValuefilePosition, V> createRecordReader(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		return new ValuefileRecordReader<V>();
	}
	
	protected static class ValuefileRecordReader<V> extends RecordReader<ValuefilePosition, V> {
		protected FSDataInputStream in;
		protected FileSplit split;
		protected long position;

		protected Serialization serde;
		protected Class<V> valueType;
		
		protected ValuefilePosition currentKey;
		protected V currentValue;
		
		@SuppressWarnings("unchecked")
		@Override
		public void initialize(InputSplit s, TaskAttemptContext context) throws IOException, InterruptedException {
			serde = Serializations.get(getSerializationClassName(context.getConfiguration()));
			valueType = (Class<V>) getValueType(context.getConfiguration());
			
			split = (FileSplit) s;
			in = split.getPath().getFileSystem(context.getConfiguration()).open(split.getPath());
			position = 0;
			while(position < split.getStart())
				skipKeyValue();
		}

		protected void skipKeyValue() throws IOException {
			byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
			in.readFully(lbytes);
			long vlen = ByteArrays.toLong(lbytes, 0);
			position += ByteArrays.LENGTH_LONG + vlen;
		}
		
		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			if(position >= split.getStart() + split.getLength())
				return false;
			
			currentKey = new ValuefilePosition(split.getPath().toString(), position);

			in.seek(position);
			byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
			in.readFully(lbytes);
			long vlen = ByteArrays.toLong(lbytes, 0);
			
			in.seek(position + ByteArrays.LENGTH_LONG);
			Deserializer<V> vdes = serde.newDeserializer(in, valueType);
			currentValue = vdes.read();
			
			position += ByteArrays.LENGTH_LONG + vlen;
			
			return true;
		}

		@Override
		public ValuefilePosition getCurrentKey() throws IOException, InterruptedException {
			return currentKey;
		}

		@Override
		public V getCurrentValue() throws IOException, InterruptedException {
			return currentValue;
		}

		@Override
		public float getProgress() throws IOException, InterruptedException {
			return (in.getPos() - split.getStart()) / (float) split.getLength();
		}

		@Override
		public void close() throws IOException {
			in.close();
		}
	}
}
