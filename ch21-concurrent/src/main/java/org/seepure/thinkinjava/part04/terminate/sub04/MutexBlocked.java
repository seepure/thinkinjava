package org.seepure.thinkinjava.part04.terminate.sub04;

public class MutexBlocked implements Runnable{
	BlockedMutex blocked = new BlockedMutex();
	@Override
	public void run() {
		System.out.println("Waiting for f() in BlockedMutex");
		blocked.f();
		System.out.println("Broken out of blocked call");
	}
}
