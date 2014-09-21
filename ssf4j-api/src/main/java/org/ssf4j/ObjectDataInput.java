package org.ssf4j;

import java.io.DataInput;
import java.io.IOException;

public interface ObjectDataInput extends DataInput {
	/**
	 * Calls {@link #read(boolean, Class)} with {@code nulls == false}
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T read(Class<T> type) throws IOException;
	/**
	 * Calls {@link #read(boolean, boolean, Class)} with {@code polymorphic == false}
	 * @param nulls
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T read(boolean nulls, Class<T> type) throws IOException;
	/**
	 * Reads an object.  If {@code nulls == true} then the object read may be null.
	 * If {@code polymorphic == true} then the actual class of the object is read
	 * and used rather than the argument class.
	 * @param nulls
	 * @param polymorphic
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public <T> T read(boolean nulls, boolean polymorphic, Class<T> type) throws IOException;
}
