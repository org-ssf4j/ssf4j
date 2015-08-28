package org.ssf4j.datafile.mapreduce;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.datafile.hashfile.HashFileWriter;
import org.ssf4j.datafile.hashfile.MessageDigestUtil;

public class HashfileOutputFormat<K, V> extends OutputFormat<K, V> {
	private static final String PREFIX = HashfileOutputFormat.class.getName();
	
	public static final String OUTPUT_PATH_KEY = PREFIX + ".output_path";
	public static final String SERIALIZATION_CLASS_KEY = PREFIX + ".serialization_class";
	public static final String KEY_TYPE_KEY = PREFIX + ".key_type";
	public static final String VALUE_TYPE_KEY = PREFIX + ".value_type";
	
	public static final String DEFAULT_SERIALIZATION_CLASS = Serializations.AVRO_BINARY;

	public static void setOutputPath(Job job, Path outputPath) {
		job.getConfiguration().set(OUTPUT_PATH_KEY, outputPath.toString());
	}
	
	public static void setSerializationClassName(Job job, String serializationClassName) {
		job.getConfiguration().set(SERIALIZATION_CLASS_KEY, serializationClassName);
	}
	
	public static void setSerializationClass(Job job, Class<? extends Serialization> serializationClass) {
		if(serializationClass.isInterface())
			throw new IllegalArgumentException();
		job.getConfiguration().setClass(SERIALIZATION_CLASS_KEY, serializationClass, Serialization.class);
	}
	
	public static void setKeyType(Job job, Class<?> keyType) {
		job.getConfiguration().set(KEY_TYPE_KEY, keyType.getName());
	}
	
	public static void setValueType(Job job, Class<?> valueType) {
		job.getConfiguration().set(VALUE_TYPE_KEY, valueType.getName());
	}
	
	protected static Path getOutputPath(TaskAttemptContext ctx, String suffix) {
		return getOutputPath(ctx.getConfiguration(), suffix, ctx.getTaskAttemptID().getTaskID().getId());
	}
	
	protected static Path getOutputPath(Configuration c, String suffix, int taskId) {
		return new Path(c.get(OUTPUT_PATH_KEY) + taskId + suffix);
	}
	
	protected static Path getKeysOutputPath(TaskAttemptContext ctx) {
		return getOutputPath(ctx, ".keys");
	}
	
	protected static Path getValuesOutputPath(TaskAttemptContext ctx) {
		return getOutputPath(ctx, ".values");
	}
	
	protected static Path getKeysOutputPath(Configuration c, int taskId) {
		return getOutputPath(c, ".keys", taskId);
	}
	
	protected static Path getValuesOutputPath(Configuration c, int taskId) {
		return getOutputPath(c, ".values", taskId);
	}
	
	protected static Path getKeysTempOutputPath(TaskAttemptContext ctx) {
		return getOutputPath(ctx, ".keys.tmp");
	}
	
	protected static Path getValuesTempOutputPath(TaskAttemptContext ctx) {
		return getOutputPath(ctx, ".values.tmp");
	}
	
	protected static String getSerializationClassName(Configuration c) {
		return c.get(SERIALIZATION_CLASS_KEY, DEFAULT_SERIALIZATION_CLASS);
	}
	
	protected static Class<? extends Serialization> getSerializationClass(Configuration c) {
		return c.getClass(SERIALIZATION_CLASS_KEY, c.getClassByNameOrNull(DEFAULT_SERIALIZATION_CLASS).asSubclass(Serialization.class), Serialization.class);
	}
	
	protected static Class<?> getKeyType(Configuration c) {
		return c.getClass(KEY_TYPE_KEY, null);
	}
	
	protected static Class<?> getValueType(Configuration c) {
		return c.getClass(VALUE_TYPE_KEY, null);
	}
	
	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new HashfileRecordWriter<K, V>(context);
	}

	@Override
	public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {
		Configuration c = context.getConfiguration();
		if(c.get(OUTPUT_PATH_KEY) == null)
			throw new IOException(OUTPUT_PATH_KEY + " not specified");
		try {
			Class<?> cls = getSerializationClass(c);
			if(cls.isInterface())
				throw new RuntimeException();
		} catch(Exception e) {
			throw new IOException("Invalid serialization class: " + getSerializationClassName(c));
		}
		if(c.getClass(KEY_TYPE_KEY, null) == null)
			throw new IOException(KEY_TYPE_KEY + " not specified");
		if(c.getClass(VALUE_TYPE_KEY, null) == null)
			throw new IOException(VALUE_TYPE_KEY + " not specified");
		for(int taskId = 0; taskId < context.getNumReduceTasks(); taskId++) {
			Path keysPath = getKeysOutputPath(c, taskId);
			Path valuesPath = getValuesOutputPath(c, taskId);
			if(keysPath.getFileSystem(c).exists(keysPath))
				throw new IOException(keysPath + " exists");
			if(valuesPath.getFileSystem(c).exists(valuesPath))
				throw new IOException(valuesPath + " exists");
		}
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new HashfileOutputCommitter();
	}

	protected static class HashfileRecordWriter<K, V> extends RecordWriter<K, V> {
		protected TaskAttemptContext context;
		
		protected HashFileWriter<K, V> writer;
		
		@SuppressWarnings("unchecked")
		public HashfileRecordWriter(TaskAttemptContext context) throws IOException {
			this.context = context;
			
			Configuration c = context.getConfiguration();
			
			Path keysPath = getKeysTempOutputPath(context);
			Path valuesPath = getValuesTempOutputPath(context);
			
			OutputStream keysOut = keysPath.getFileSystem(c).create(keysPath, true);
			OutputStream valuesOut = valuesPath.getFileSystem(c).create(valuesPath, true);
			
			writer = new HashFileWriter<K, V>(
					MessageDigestUtil.SHA1, 
					Serializations.get(getSerializationClassName(c)), 
					(Class<K>) getKeyType(c), 
					(Class<V>) getValueType(c), 
					keysOut, 
					valuesOut);
		}
		
		@Override
		public void write(K key, V value) throws IOException, InterruptedException {
			writer.put(key, value);
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			writer.close();
		}
	}

	protected static class HashfileOutputCommitter extends OutputCommitter {
		@Override
		public void setupTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path keysTempPath = getKeysTempOutputPath(taskContext);
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			keysTempPath.getFileSystem(c).delete(keysTempPath, false);
			valuesTempPath.getFileSystem(c).delete(valuesTempPath, false);
		}
	
		@Override
		public void setupJob(JobContext jobContext) throws IOException {
		}
	
		@Override
		public boolean needsTaskCommit(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path keysTempPath = getKeysTempOutputPath(taskContext);
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			return keysTempPath.getFileSystem(c).exists(keysTempPath) || valuesTempPath.getFileSystem(c).exists(valuesTempPath);
		}
	
		@Override
		public void commitTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path keysTempPath = getKeysTempOutputPath(taskContext);
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			Path keysPath = getKeysOutputPath(taskContext);
			Path valuesPath = getValuesOutputPath(taskContext);
			
			keysPath.getFileSystem(c).delete(keysPath, false);
			keysPath.getFileSystem(c).rename(keysTempPath, keysPath);
			
			valuesPath.getFileSystem(c).delete(valuesPath, false);
			valuesPath.getFileSystem(c).rename(valuesTempPath, valuesPath);
		}
	
		@Override
		public void abortTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path keysTempPath = getKeysTempOutputPath(taskContext);
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			keysTempPath.getFileSystem(c).delete(keysTempPath, false);
			valuesTempPath.getFileSystem(c).delete(valuesTempPath, false);
		}
	}

}
