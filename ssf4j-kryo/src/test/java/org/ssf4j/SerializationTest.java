package org.ssf4j;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SerializationTest<T> extends AbstractSerializationTest<T> {
	@Parameters
	public static Iterable<Object[]> params() {
		List<Object[]> p = new ArrayList<Object[]>();
		
		p.add(new Object[] {12.34, Double.class});
		
		return p;
	}
	
	public SerializationTest(T obj, Class<T> type) {
		super(obj, type);
	}

}
