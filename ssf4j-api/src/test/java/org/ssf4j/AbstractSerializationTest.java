package org.ssf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractSerializationTest<T> {
	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		
		p.add(new Object[] {12.34, Double.class});
		
		return p;
	}
	
	protected T obj;
	protected Class<T> type;
	
	public AbstractSerializationTest(T obj, Class<T> type) {
		this.obj = obj;
		this.type = type;
	}
	
	@Test
	public void testSerialization() throws Exception {
		Serialization s = AutomaticSerialization.get();
		s.newSerializer(new ByteArrayOutputStream(), type).write(obj);
	}
	
	@Test
	public void testDeserialization() throws Exception {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		Serialization s = AutomaticSerialization.get();
		Serializer<T> se = s.newSerializer(b, type);
		se.write(obj);
		se.close();
		Deserializer<T> de = s.newDeserializer(new ByteArrayInputStream(b.toByteArray()), type);
		T sobj = de.read();
		Assert.assertEquals(obj, sobj);
	}
}
