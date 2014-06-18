package org.ssf4j;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

public class SerializationTest<T> extends AbstractSerializationTest<T> {
	public SerializationTest(T obj, Class<T> type) {
		super(obj, type);
	}

}
