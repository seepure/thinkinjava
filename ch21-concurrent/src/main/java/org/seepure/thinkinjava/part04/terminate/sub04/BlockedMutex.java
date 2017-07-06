package org.seepure.thinkinjava.part04.terminate.sub04;

import java.util.concurrent.locks.ReentrantLock;

public class BlockedMutex {
	private ReentrantLock lock = new ReentrantLock();
	public BlockedMutex() {
		lock.lock();
	}
	
	public void f() {
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			System.out.println("Interrupted from lock acquisition in f()");
		}
	}
}

