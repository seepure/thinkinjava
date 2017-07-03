package org.seepure.thinkinjava.part04.terminate.sub02;

import java.util.concurrent.TimeUnit;

public class SleepBlocked implements Runnable{

	public void run() {
		try {
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (InterruptedException e) {
			System.out.println("InterruptedException");
		}
		System.out.println("Exiting SleepBlocked.run()");
	}

}
