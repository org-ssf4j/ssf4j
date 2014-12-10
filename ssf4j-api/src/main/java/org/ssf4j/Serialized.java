package org.ssf4j;

import java.io.IOException;

public interface Serialized {
	public void write(ObjectDataOutput out) throws IOException;
	public void read(ObjectDataInput in) throws IOException;
}
