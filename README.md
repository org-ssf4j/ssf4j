<strong>Simple Serialization Facade for Java</strong>

SSF4J is a wrapper around other serialization frameworks.  It doesn't do any serialization itself; instead, it allows 
you to write code that takes advantage of serialization without having to know the details of the serialization engine 
in question.  It is written in the spirit of the (unrelated) project [SLF4J](http://www.slf4j.org), which provides a 
facade for logging rather than serialization.

SSF4J is intended to be used as a supporting library for library development.  In your library you rely on SSF4J for 
serialization, so that users of your library can specify the serialization mechanism appropriate for their use case.  
For example, if your library needs to buffer data to disk, it may not be appropriate to choose a specific serialization 
mechanism.  Instead, you could use SSF4J and let users of your library provide the appropriate serialization adapter, 
such as `ssf4j-avro-json` or `ssf4j-kryo`.

To use SSF4J in your library, include the `ssf4j-api` artifact in your `pom.xml`.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-api</artifactId>
     <version>0.4</version>
    </dependency>

The current unstable is `0.5-SNAPSHOT`.

## Usage 

### Typical Usage 

Serializing an object of type Foo:

    Serialization serde = Serializations.get(Serializations.KRYO);
    Serializer<Foo> sfoo = serde.newSerializer(out, Foo.class);
    sfoo.write(someFoo);

Deserializing an object of type Foo:

    Serialization serde = Serializations.get(Serializations.KRYO);
    Deserializer<Foo> dfoo = serde.newDeserializer(in, Foo.class);
    someFoo = dfoo.read();

### MapReduce 

SSF4J has artifacts for dealing with Apache Hadoop, both as a consumer and producer of data.  The `ssf4j-datafile-hadoop`
artifact can be used to read the "hashfile"" pairs produced by `ssf4j-datafile`, or just the "valuefile" portion of 
the hashfile, loading from HDFS.  It contains a useful Avro object that holds the path of a file on HDFS and an offset within
that file, that is used with the `ssf4j-datafile-mapreduce` input and output formats.

The `ssf4j-datafile-hadoop` and `ssf4j-datafile-mapreduce` artifacts assume Apache Hadoop 2.2.

#### MapReduce Maven Artifacts 

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-datafile-hadoop</artifactId>
     <version>0.4</version>
    </dependency>

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-datafile-mapreduce</artifactId>
     <version>0.4</version>
    </dependency>

#### MapReduce Usage 

The following Java snippet is a simple Hadoop tool that ingests standard Avro container files and produces SSF4J valuefiles.

```java
    import java.io.IOException;
    import java.util.Arrays;
    
    import org.apache.avro.Schema;
    import org.apache.avro.mapred.AvroKey;
    import org.apache.avro.mapreduce.AvroJob;
    import org.apache.avro.mapreduce.AvroKeyInputFormat;
    import org.apache.commons.cli.CommandLine;
    import org.apache.commons.cli.Options;
    import org.apache.hadoop.conf.Configuration;
    import org.apache.hadoop.conf.Configured;
    import org.apache.hadoop.fs.Path;
    import org.apache.hadoop.io.NullWritable;
    import org.apache.hadoop.mapreduce.Job;
    import org.apache.hadoop.mapreduce.Reducer;
    import org.apache.hadoop.util.GenericOptionsParser;
    import org.apache.hadoop.util.Tool;
    import org.mitre.leviathan.ThreadedTrack;
    import org.ssf4j.datafile.mapreduce.ValuefileOutputFormat;
    
    public abstract class AbstractValuefileTool extends Configured implements Tool {
    	private static final Options OPT = new Options();
    	static {
    		OPT.addOption("o", "out", true, "output path parent dir for hfiles");
    		OPT.addOption("s", "split-size", true, "size of an input split (bytes)");
    		OPT.addOption("r", "reducers", true, "number of reducers");
    	}
    
    	protected abstract Class<?> getType();
    	protected abstract Schema getSchema();
    	
    	@Override
    	public int run(String[] args) throws Exception {
    		System.out.println(this + " <-" + Arrays.toString(args));
    		
    		Configuration conf = getConf();
    		CommandLine cli = new GenericOptionsParser(conf, OPT, args).getCommandLine();
    
    		Job job = Job.getInstance(conf);
    		job.setJarByClass(AbstractValuefileTool.class);
    		job.setJobName(getClass().getName() + " " + Arrays.toString(cli.getArgs()));
    
    		job.setInputFormatClass(AvroKeyInputFormat.class);
    		AvroKeyInputFormat.setMinInputSplitSize(job, Long.parseLong(cli.getOptionValue('s', String.valueOf(1024 * 1024))));
    		AvroKeyInputFormat.setMaxInputSplitSize(job, Long.parseLong(cli.getOptionValue('s', String.valueOf(1024 * 1024))));
    
    		for(String input : cli.getArgs()) {
    			AvroKeyInputFormat.addInputPath(job, new Path(input));
    		}
    
    		AvroJob.setInputKeySchema(job, getSchema());
    		AvroJob.setMapOutputKeySchema(job, getSchema());
    		
    		job.setMapOutputKeyClass(AvroKey.class);
    		job.setMapOutputValueClass(NullWritable.class);
    		
    		job.setOutputKeyClass(NullWritable.class);
    		job.setOutputValueClass(getType());
    		
    		job.setReducerClass(ValuefileReducer.class);
    		job.setNumReduceTasks(Integer.parseInt(cli.getOptionValue('r', "1")));
    		
    		Path out = new Path(cli.getOptionValue('o'));
    		ValuefileOutputFormat.setOutputPath(job, out);
    		ValuefileOutputFormat.setValueType(job, getType());
    		job.setOutputFormatClass(ValuefileOutputFormat.class);
    
    		return job.waitForCompletion(true) ? 0 : -1;
    	}
    
    	public static class ValuefileReducer extends Reducer<AvroKey<?>, NullWritable, NullWritable, Object> {
    		@Override
    		protected void reduce(AvroKey<?> key, Iterable<NullWritable> values, Context context)
    				throws IOException, InterruptedException {
    			context.write(NullWritable.get(), key.datum());
    		}
    	}
```

## Supported Serialization Mechanisms 

### JDK 

JDK serialization is the JRE's built-in serialization mechanism.  It can only serialize objects that implement java.io.Serializable

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-jdk</artifactId>
     <version>0.4</version>
    </dependency>

### [Kryo](https://github.com/EsotericSoftware/kryo) 

Kryo can serialize pretty much anything, and fast.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-kryo</artifactId>
     <version>0.4</version>
    </dependency>

### [avro](http://avro.apache.org) (binary/json output) 

Avro can only serialize objects generated from avro schemas, enums, and primitive types.

#### For binary encoding 

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-avro-binary</artifactId>
     <version>0.4</version>
    </dependency>

#### For JSON encoding 

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-avro-json</artifactId>
     <version>0.4</version>
    </dependency>

### [Jackson](http://wiki.fasterxml.com/JacksonHome) 

Jackson can serialize most things, but fails on a few cases.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-jackson</artifactId>
     <version>0.4</version>
    </dependency>

### [Purple Jrank](http://www.purplejrank.org) 

Purple Jrank can serialize anything the JDK can serialize.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-purplejrank</artifactId>
     <version>0.4</version>
    </dependency>

### [XStream](http://xstream.codehaus.org) 

XStream can serialize pretty much anything.

    <dependency>
     <groupId>org.ssf4j</groupId>
     <artifactId>ssf4j-xstream</artifactId>
     <version>0.4</version>
    </dependency>
