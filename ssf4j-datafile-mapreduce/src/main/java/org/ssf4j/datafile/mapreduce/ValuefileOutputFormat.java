package org.ssf4j.datafile.mapreduce;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.ssf4j.Serialization;
import org.ssf4j.Serializations;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.hashfile.ByteArrays;
import org.ssf4j.datafile.hashfile.CountingOutputStream;
import org.ssf4j.datafile.hashfile.NullOutputStream;

public class ValuefileOutputFormat<V> extends OutputFormat<NullWritable, V> {
	private static final String PREFIX = ValuefileOutputFormat.class.getName();
	
	public static final String OUTPUT_PATH_KEY = PREFIX + ".output_path";
	public static final String SERIALIZATION_CLASS_KEY = PREFIX + ".serialization_class";
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
	
	public static void setValueType(Job job, Class<?> valueType) {
		job.getConfiguration().set(VALUE_TYPE_KEY, valueType.getName());
	}
	
	protected static Path getOutputPath(TaskAttemptContext ctx, String suffix) {
		return getOutputPath(ctx.getConfiguration(), suffix, ctx.getTaskAttemptID().getTaskID().getId());
	}
	
	protected static Path getOutputPath(Configuration c, String suffix, int taskId) {
		return new Path(c.get(OUTPUT_PATH_KEY) + "_" + taskId + suffix);
	}
	
	protected static Path getValuesOutputPath(TaskAttemptContext ctx) {
		return getOutputPath(ctx, ".values");
	}
	
	protected static Path getValuesOutputPath(Configuration c, int taskId) {
		return getOutputPath(c, ".values", taskId);
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
	
	protected static Class<?> getValueType(Configuration c) {
		return c.getClass(VALUE_TYPE_KEY, null);
	}
	
	@Override
	public RecordWriter<NullWritable, V> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new ValuefileRecordWriter<V>(context);
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
		if(c.getClass(VALUE_TYPE_KEY, null) == null)
			throw new IOException(VALUE_TYPE_KEY + " not specified");
		for(int taskId = 0; taskId < context.getNumReduceTasks(); taskId++) {
			Path valuesPath = getValuesOutputPath(c, taskId);
			if(valuesPath.getFileSystem(c).exists(valuesPath))
				throw new IOException(valuesPath + " exists");
		}
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new HashfileOutputCommitter();
	}

	protected static class ValuefileRecordWriter<V> extends RecordWriter<NullWritable, V> {
		protected TaskAttemptContext context;
		
		protected OutputStream valuesOut;
		protected Class<V> valueType;
		protected Serialization serde;
		protected Serializer<V> ser;
		
		@SuppressWarnings("unchecked")
		public ValuefileRecordWriter(TaskAttemptContext context) throws IOException {
			this.context = context;
			
			Configuration c = context.getConfiguration();
			
			Path valuesPath = getValuesTempOutputPath(context);
			
			valuesOut = valuesPath.getFileSystem(c).create(valuesPath, true);
			valueType = (Class<V>) getValueType(c);
			serde = Serializations.get(getSerializationClassName(c));
			ser = serde.newSerializer(valuesOut, valueType);
		}
		
		@Override
		public void write(NullWritable key, V value) throws IOException, InterruptedException {
			CountingOutputStream cout = new CountingOutputStream(NullOutputStream.get());
			
			Serializer<V> vser = serde.newSerializer(cout, valueType);
			vser.write(value);
			vser.close();
			long vlen = cout.getLength();
			
			byte[] lbytes = new byte[ByteArrays.LENGTH_LONG];
			ByteArrays.toBytes(lbytes, 0, vlen);
			valuesOut.write(lbytes);

			ser.write(value);
			ser.flush();
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			ser.close();
		}
	}

	protected static class HashfileOutputCommitter extends OutputCommitter {
		@Override
		public void setupTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			valuesTempPath.getFileSystem(taskContext.getConfiguration()).mkdirs(valuesTempPath.getParent());
			
			valuesTempPath.getFileSystem(c).delete(valuesTempPath, false);
		}
	
		@Override
		public void setupJob(JobContext jobContext) throws IOException {
		}
	
		@Override
		public boolean needsTaskCommit(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			return valuesTempPath.getFileSystem(c).exists(valuesTempPath);
		}
	
		@Override
		public void commitTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			Path valuesPath = getValuesOutputPath(taskContext);
			
			valuesPath.getFileSystem(c).delete(valuesPath, false);
			valuesPath.getFileSystem(c).rename(valuesTempPath, valuesPath);
		}
	
		@Override
		public void abortTask(TaskAttemptContext taskContext) throws IOException {
			Configuration c = taskContext.getConfiguration();
			
			Path valuesTempPath = getValuesTempOutputPath(taskContext);
			
			valuesTempPath.getFileSystem(c).delete(valuesTempPath, false);
		}
	}

}
