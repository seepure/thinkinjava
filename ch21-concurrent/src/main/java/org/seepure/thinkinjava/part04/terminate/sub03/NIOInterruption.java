package org.seepure.thinkinjava.part04.terminate.sub03;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 从这个例子可以看出shutdown和 shutdownNow的区别，
 * shutdownNow一旦被调用，会立刻向executorService持有的所有线程调用 cancel方法，
 * shutdown则不会
 */
public class NIOInterruption {
	public static void main(String[] args) throws Exception {
		//testShutdown();
		testShutdownNow();
	}

	public static void testShutdown() throws Exception {
		int port = 8004;
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8004);
		InetSocketAddress isa =
				new InetSocketAddress("localhost", port);
		SocketChannel sc1 = SocketChannel.open(isa);
		SocketChannel sc2 = SocketChannel.open(isa);

		Future<?> f = exec.submit(new NIOBlocked(sc1));
		exec.execute(new NIOBlocked(sc2));
		exec.shutdown();
		TimeUnit.SECONDS.sleep(1);
		f.cancel(true);
		TimeUnit.SECONDS.sleep(1);
		sc2.close();
	}

	public static void testShutdownNow() throws Exception {
		int port = 8004;
		ExecutorService exec = Executors.newCachedThreadPool();
		ServerSocket server = new ServerSocket(8004);
		InetSocketAddress isa =
				new InetSocketAddress("localhost", port);
		SocketChannel sc1 = SocketChannel.open(isa);
		SocketChannel sc2 = SocketChannel.open(isa);

		Future<?> f = exec.submit(new NIOBlocked(sc1));
		exec.execute(new NIOBlocked(sc2));
		exec.shutdownNow();
		TimeUnit.SECONDS.sleep(1);
		TimeUnit.SECONDS.sleep(1);
	}
}
