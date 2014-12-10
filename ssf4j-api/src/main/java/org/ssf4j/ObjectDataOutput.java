package org.ssf4j;

import java.io.DataOutput;
import java.io.IOException;

public interface ObjectDataOutput extends DataOutput {
	/**
	 * Calls {@link #write(boolean, Object)} with {@code nulls == false}
	 * @param object
	 * @throws IOException
	 */
	public void write(Object object) throws IOException;
	/**
	 * Calls {@link #write(boolean, boolean, Object)} with {@code polymorphic == false}
	 * @param nulls
	 * @param object
	 * @throws IOException
	 */
	public void write(boolean nulls, Object object) throws IOException;
	/**
	 * Writes the argument object.  If {@code nulls == true} then the object can be null.
	 * If {@code polymorphic == true} then the class of the object is written before writing
	 * the object content.
	 * @param nulls
	 * @param polymorphic
	 * @param object
	 * @throws IOException
	 */
	public void write(boolean nulls, boolean polymorphic, Object object) throws IOException;
}
