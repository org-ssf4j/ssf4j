package org.ssf4j.datafile;

import static org.ssf4j.Serializations.JACKSON;
import static org.ssf4j.Serializations.JDK;
import static org.ssf4j.Serializations.KRYO;
import static org.ssf4j.Serializations.PURPLEJRANK;
import static org.ssf4j.Serializations.XSTREAM;

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

@RunWith(Parameterized.class)
public class ImmutableListCacheTest {
	@Parameters
	public static Iterable<Object[]> params() {
		List<String> serdes = Arrays.asList(KRYO, JACKSON, JDK, PURPLEJRANK, XSTREAM);
		List<Object[]> ret = new ArrayList<Object[]>();
		for(String serde : serdes)
			ret.add(new Object[] {serde});
		return ret;
	}
	
	protected String serde;
	
	public ImmutableListCacheTest(String serde) {
		this.serde = serde;
	}
	
	@Test
	public void testCache() throws Exception {
		int NUM = 64;
		int SIZE = 8 * 1024 * 1024;
		int BT = 8;
		
		File dir = new File("target/tmp/ImmutableListCacheTest");
		dir.mkdirs();
		File f = new File(dir, serde);
		f.delete();
		
		DataFile<byte[]> df = new DataFile<byte[]>(f, Serializations.get(serde), byte[].class);
		
		System.out.println("Writing " + f);
		Serializer<byte[]> ser = df.newSerializer();
		long start = System.currentTimeMillis();
		for(int i = 1; i <= NUM; i++) {
			ser.write(new byte[SIZE]);
			System.out.print(".");
			if((i % 80) == 0)
				System.out.println(i);
		}
		System.out.println();
		ser.close();
		long stop = System.currentTimeMillis();
		System.out.println("Took " + ((stop - start) / 1000.) + " seconds");
		
		DataFileDeserializer<byte[]> de = df.newDeserializer();
		List<byte[]> lde = new ImmutableListCache<byte[]>(de);

		System.out.println("Reading " + f);
		start = System.currentTimeMillis();
		for(int i = 1; i <= NUM; i++) {
			Assert.assertEquals(SIZE, lde.get(i-1).length);
			System.out.print(".");
			if((i % 80) == 0)
				System.out.println(i);
		}
		System.out.println();
		stop = System.currentTimeMillis();
		System.out.println("Took " + ((stop - start) / 1000.) + " seconds");
		
		System.out.println("Re-reading " + f);
		start = System.currentTimeMillis();
		for(int i = 1; i <= NUM; i++) {
			Assert.assertEquals(SIZE, lde.get(i-1).length);
			System.out.print(".");
			if((i % 80) == 0)
				System.out.println(i);
		}
		System.out.println();
		stop = System.currentTimeMillis();
		System.out.println("Took " + ((stop - start) / 1000.) + " seconds");
		
		System.out.println("Backtrack-reading " + f);
		start = System.currentTimeMillis();
		for(int i = BT; i <= NUM; i++) {
			for(int j = i - BT; j < i; j++)
				Assert.assertEquals(SIZE, lde.get(j).length);
			System.out.print(".");
			if(((i - BT + 1) % 80) == 0)
				System.out.println(i);
		}
		System.out.println();
		stop = System.currentTimeMillis();
		System.out.println("Took " + ((stop - start) / 1000.) + " seconds");

		de.close();
		f.delete();
	}
}
