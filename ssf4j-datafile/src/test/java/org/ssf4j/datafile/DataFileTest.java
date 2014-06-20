package org.ssf4j.datafile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.ssf4j.Serializations;
import org.ssf4j.Serializer;
import org.ssf4j.datafile.DataFile;

import static org.ssf4j.Serializations.*;

@RunWith(Parameterized.class)
public class DataFileTest {
	@Parameters
	public static Iterable<Object[]> params() {
		List<String> serdes = Arrays.asList(AVRO_BINARY, AVRO_JSON, JACKSON, JDK, KRYO, PURPLEJRANK, XSTREAM);
		List<Object[]> ret = new ArrayList<Object[]>();
		for(String serde : serdes)
			ret.add(new Object[] {serde});
		return ret;
	}
	
	protected String serde;
	
	public DataFileTest(String serde) {
		this.serde = serde;
	}
	
	@Test
	public void testDataFile() throws Exception {
		File dir = new File("target/tmp/DataFileTest");
		dir.mkdirs();
		File f = new File(dir, serde);
		try {
			DataFile<Integer> df = new DataFile<Integer>(f, Serializations.get(this.serde), Integer.class);
			
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
			de.close();
			
			MappedDataFileDeserializer<Integer> mde = df.newMappedDeserializer();
			mde.load();
			Assert.assertEquals(0, (int) mde.read());
			Assert.assertEquals(1, (int) mde.read());
			Assert.assertEquals(2, (int) mde.read());
			mde.seek(4);
			Assert.assertEquals(4, (int) mde.read());
			Assert.assertEquals(3, (int) mde.read(3));
			mde.close();
			
			f.delete();
		} finally {
		}
	}
}
