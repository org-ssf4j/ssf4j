package org.ssf4j.datafile;
import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.ssf4j.Deserializer;
import org.ssf4j.Serializations;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.DataFile;


public class DataFileTest {
	@Test
	public void testDataFile() throws Exception {
		File f = File.createTempFile("DataFileTest", ".tmp");
		try {
			DataFile<Integer> df = new DataFile<Integer>(f, Serializations.get(Serializations.KRYO), Integer.class);
			
			Serializer<Integer> ser = df.newSerializer();
			
			ser.write(0);
			ser.write(1);
			ser.write(2);
			ser.write(3);
			ser.write(4);
			ser.close();
			
			DataFileDeserializer<Integer> de = df.newDeserializer();
			Assert.assertEquals(0, (int) de.read());
			Assert.assertEquals(1, (int) de.read());
			Assert.assertEquals(2, (int) de.read());
			de.seek(4);
			Assert.assertEquals(4, (int) de.read());
			Assert.assertEquals(3, (int) de.read(3));
			
		} finally {
			f.delete();
		}
	}
}
