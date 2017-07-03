package org.seepure.thinkinjava.part04.terminate.sub02;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 这个小节还有几个比较重要的话题:
 * Thread.interrupt() 和 ExecutorService.shutdown、ExecutorService.shutdownNow() 的区别
 * 	1. shutdownNow 和 Thread.interrupt() 等价: 只能这么说, shutdownNow的底层就是调用了 Thread.interrupt()
 *  ，而Future.cancel()底层 也很可能是Thread.interrupt()实现的，具体还要再试试
 *  2. shutdown和 shutdownNow的区别，
 * shutdownNow一旦被调用，会立刻向executorService持有的所有线程调用 cancel方法，
 * shutdown则不会
 *  3. Thread.interrupt()是无法立刻中断线程的，它只能改变线程的
 *  interrupt status, 从而使得线程能够退出while循环
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
