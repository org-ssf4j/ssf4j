package org.ssf4j;

public class Exceptions {
	public static RuntimeException runtime(Throwable t) {
		return runtime(t, null);
	}
	
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
