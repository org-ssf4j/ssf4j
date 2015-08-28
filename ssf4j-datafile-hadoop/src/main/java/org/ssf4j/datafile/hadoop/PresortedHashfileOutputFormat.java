package org.ssf4j.datafile.hadoop;

import java.io.IOException;

import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class PresortedHashfileOutputFormat<K, V> extends HashfileOutputFormat<K, V> {

	@Override
	public RecordWriter<K, V> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
		return new PresortedHashfileRecordWriter<K, V>(context);
	}
	
	protected static class PresortedHashfileRecordWriter<K, V> extends HashfileRecordWriter<K, V> {

		public PresortedHashfileRecordWriter(TaskAttemptContext context) throws IOException {
			super(context);
		}
		
		@Override
		public void write(K key, V value) throws IOException, InterruptedException {
			writer.writePresorted(key, value);
		}
		
	}
}
