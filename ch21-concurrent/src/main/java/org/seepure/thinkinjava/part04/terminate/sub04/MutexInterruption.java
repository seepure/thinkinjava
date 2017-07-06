package org.seepure.thinkinjava.part04.terminate.sub04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * if we use ReentrantLock.lockInterruptibly(), we can interrupt the MutexBlocked.
 */
public class MutexInterruption {
	public static void main(String[] args) throws Exception {
//		Thread t = new Thread(new MutexBlocked());
//		t.start();
//		TimeUnit.MILLISECONDS.sleep(500);
//		System.out.println("Issuing t.interrupt()");
//		t.interrupt();

		ExecutorService exec = Executors.newCachedThreadPool();
		Future<?> f = exec.submit(new MutexBlocked());
		TimeUnit.MILLISECONDS.sleep(500);
		System.out.println("Issuing t.interrupt()");
		f.cancel(true);
		exec.shutdown();
	}
}
