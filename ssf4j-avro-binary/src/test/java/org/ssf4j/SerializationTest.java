package org.ssf4j;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

public class SerializationTest<T> extends AbstractSerializationTest<T> {
	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		
		p.add(new Object[] {12.34, Double.class});
		p.add(new Object[] {"Hello world", String.class});
		
		return p;
	}
	public SerializationTest(T obj, Class<T> type) {
		super(obj, type);
	}

}
