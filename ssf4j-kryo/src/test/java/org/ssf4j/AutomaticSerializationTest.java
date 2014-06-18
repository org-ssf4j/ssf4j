package org.ssf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.ssf4j.AutomaticSerialization;

public class AutomaticSerializationTest {
	@Test
	public void testNewSerializer() throws Exception {
		AutomaticSerialization.newSerializer(new ByteArrayOutputStream());
	}
	
	@Test
	public void testNewDeserializer() throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		AutomaticSerialization.newSerializer(buf).close();
		AutomaticSerialization.newDeserializer(new ByteArrayInputStream(buf.toByteArray()));
	}
}
