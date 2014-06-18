package org.ssf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.ssf4j.AutomaticSerialization;

public class AutomaticSerializationTest {
	@Test
	public void testNewSerializer() throws Exception {
		AutomaticSerialization.get().newSerializer(new ByteArrayOutputStream());
	}
	
	@Test
	public void testNewDeserializer() throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		AutomaticSerialization.get().newSerializer(buf).close();
		AutomaticSerialization.get().newDeserializer(new ByteArrayInputStream(buf.toByteArray()));
	}
}
