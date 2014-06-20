package org.ssf4j;

/**
 * Utility class for dealing with exceptions
 * @author robin
 *
 */
public class Exceptions {

	/**
	 * Convert the argument to a {@link RuntimeException} so it can be thrown.
	 * <ol>
	 * <li>If the argument is an {@link Error} then rethrow it immediately.</li>
	 * <li>If the argument is already a RuntimeException, return it.</li>
	 * <li>Finally, wrap the argument in a RuntimeException and return it.</li>
	 * </ol>
	 * @param t
	 * @param message
	 * @return
	 */
	public static RuntimeException runtime(Throwable t) {
		return runtime(t, null);
	}
	
	/**
	 * Convert the argument to a {@link RuntimeException} so it can be thrown.
	 * <ol>
	 * <li>If the argument is an {@link Error} then rethrow it immediately.</li>
	 * <li>If a message is supplied, wrap the argument in a RuntimeException with
	 * the message and return it.</li>
	 * <li>If a message is not supplied but the argument is already a RuntimeException,
	 * return it.</li>
	 * <li>Finally, wrap the argument in a RuntimeException and return it.</li>
	 * </ol>
	 * 
	 * <pre>{@code
	 * try {
	 *     // do something
	 * } catch(Throwable t) {
	 *     throw Exceptions.runtime(t, "It didn't work.");
	 * }
	 * }</pre>
	 * 
	 * 
	 * @param t
	 * @param message
	 * @return
	 */
	public static RuntimeException runtime(Throwable t, String message) {
		if(t instanceof Error)
			throw (Error) t;
		if(message != null)
			return new RuntimeException(message, t);
		if(t instanceof RuntimeException)
			return (RuntimeException) t;
		return new RuntimeException(t);
	}
}
