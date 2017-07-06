package org.seepure.thinkinjava.part04.terminate.sub03;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * NIO provides more humanize interruption. Thread.interrupt() can cause the blocked NIO
 * throw an ClosedByInterruptException.
 * What's more, we can find the difference between ExecutorService.shutdown() and ExecutorService.shutdownNow()
  * shutdownNow will immediately call the Thread.interrupt() of the threads it has. while shutdown would not do that.(???
 * it seems that jdk1.8 is not the same as before! )
 */
public class NIOInterruption {
	public static void main(String[] args) throws Exception {
		testShutdown();
		//testShutdownNow();
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
