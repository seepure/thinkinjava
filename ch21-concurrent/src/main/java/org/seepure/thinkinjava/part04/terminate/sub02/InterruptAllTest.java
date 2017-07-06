package org.seepure.thinkinjava.part04.terminate.sub02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * In sub01, we stop the thread by setting the flag to be false in the running loop(which is often a while()
 * loop) of the thread.
 * However, this approach can not stop a thread immediately who is blocked (IO blocked, sleep(), synchronize etc.). in
 * sub02, we will show you how to stop a blocked thread. In addition, we will discuss the difference among
 * Thread.interrupt() (= Future.cancel(true)),
 * ExecutorService.shutdown(), ExecutorService.shutdownNow(), ExecutorService.awaitTermination().
 *
 * 1. Let's talking about how to stop blocked thread.
 * By running this example, we find that we can only terminate(or say, interrupt) the SleepBlocked thread (In fact, we can
 * interrupt threads who are blocked by the methods might throw an InterruptedException). But we can not interrupt the
 * IOBlocked thread and SynchronizedBlocked thread by simpling calling Future.cancel(true) or Thread.interrupt().
 *  1.1 In order to stop an IOBlocked Thread, we can close the blocking IO resources of the thread,
 *  	in this example, we can do it like that:
 * 			IOBlocked.in.close();
 * 		but this approach has disadvantage because in.close() may cause the current work can not stop properly. For example,
 * 		if the Thread is writing data to Kafka, and the Kafka client is suddenly close() by your code, you might lost data.
 * 		We will show you how we properly interrupt an IOBlocked Thread in next sub part.
 * 	1.2 It seems that we can not interrupt a SynchronizedBlocked Thread . T_T. But we can interrupt the Thread who is trying
 * 	to get the lock by using the implements of Lock.
 *
 * 2. Thread.interrupt(), ExecutorService.shutdown, ExecutorService.shutdownNow()
 * 	2.1. shutdownNow equals to Thread.interrupt() , or say, shutdownNow based on Thread.interrupt()
 *  , and Future.cancel(true) is also based on Thread.interrupt().
 *  2.2. shutdown and shutdownNow :
 * Once shutdownNow is called，ExecutorService will call Future.cancel(true) to all the threads it has.
 * While shutdown will not.
 *  2.3. Thread.interrupt() can not terminate thread immediately, it can only change the
 *  interrupt status of threads, and then break the while(interrupted()) loop of the running thread (if the thread has
 *  such loop)
 *
 */
public class InterruptAllTest {
	private static ExecutorService exec = 
		Executors.newCachedThreadPool();
	static void test(Runnable r) throws InterruptedException {
		Future<?> f = exec.submit(r);
		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println("Interrupting "+r.getClass().getName());
		f.cancel(true);
		System.out.println("Interrupt sent to "+r.getClass().getName());
	}
	
	public static void main(String[] args) throws Exception {
		final Condition bool = new Condition();
		Thread t = new Thread() {
			@Override
			public void run() {
				while (bool.getaBoolean() /*&& !interrupted()*/){
					int j=1;
					for (int k=0; k < 3; k++) {
						for (int i = 1; i < 250000000; i++) {
							j = j * i;
						}
					}
					System.out.println("haha can you interrupt me? "+j);
				}
				System.out.println("oh i am killed");
			}
		};
		t.start();
		TimeUnit.SECONDS.sleep(3);
		System.out.println("Try to interrupt");
		t.interrupt();
		TimeUnit.SECONDS.sleep(3);
		System.out.println("Try to set boolean");
		bool.setaBoolean(false);
		TimeUnit.SECONDS.sleep(1);
		System.out.println();
		System.out.println();
		test(new SleepBlocked());
		test(new IOBlocked(System.in));
		test(new SynchronizedBlocked());
		TimeUnit.SECONDS.sleep(3);
		System.out.println("Aborting with System.exit(0)");
		System.exit(0);
	}

	static class Condition {
		private volatile Boolean aBoolean = new Boolean(true);

		public Boolean getaBoolean() {
			return aBoolean;
		}

		public void setaBoolean(Boolean aBoolean) {
			this.aBoolean = aBoolean;
		}
	}
}
