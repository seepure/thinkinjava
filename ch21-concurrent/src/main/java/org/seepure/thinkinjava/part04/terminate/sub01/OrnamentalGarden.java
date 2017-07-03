package org.seepure.thinkinjava.part04.terminate.sub01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * an example shows how we terminate a thread by add a boolean field running (canceled .etc).
 * By this way, the run() method of the implement of the Runnable or Thread is below:
 *  public void run() {
 *      while(running) {
 *          //.. do something
 *      }
 *  }
 *
 *  we can set running = false to terminate the thread.
 */
public class OrnamentalGarden {
	public static void main(String[] args) throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i=0; i < 5; i++) {
			exec.execute(new Entrance(i));
		}
		try {
			TimeUnit.MILLISECONDS.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Entrance.cancel();
		exec.shutdown();
		if (!exec.awaitTermination(25, TimeUnit.MILLISECONDS))
			System.out.println("Some Task were not terminated");
		System.out.println("Total: "+Entrance.getTotalCount());
		System.out.println("Sum of Entrances: "+ Entrance.sumEntrances());
	}
}

class Count {
	private int count = 0;

	public /*synchronized*/ int increment() {
		return ++count;
	}

	public synchronized int value() {
		return count;
	}
}

class Entrance implements Runnable {
	private static Count count = new Count();
	private static List<Entrance> entrances =
			new ArrayList<Entrance>();
	private int number = 0;
	private final int id;
	private static volatile boolean canceled = false;
	public static void cancel() {
		canceled = true;
	}
	public Entrance(int id) {
		this.id = id;
		entrances.add(this);
	}

	public void run() {
		while (!canceled) {
			synchronized (this) {
				++number;
			}
			System.out.println(this + " Total: "+
					count.increment());
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Stopping "+this);
	}
	public synchronized int getValue() {
		return number;
	}
	public String toString() {
		return "Entrance "+ id + ": "+getValue();
	}

	public static int getTotalCount() {
		return count.value();
	}

	public static int sumEntrances() {
		int sum = 0;
		for (Entrance e : entrances) {
			sum += e.getValue();
		}
		return sum;
	}
}
