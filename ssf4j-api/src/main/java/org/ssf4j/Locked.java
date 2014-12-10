package org.ssf4j;

import java.util.concurrent.locks.ReentrantLock;

public interface Locked {
	public ReentrantLock getLock();
}
