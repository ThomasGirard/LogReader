package ch.arrg.logreader.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Throttle {

	public static void main(String[] args) {
		try {
			long authorizedDelay = 100;

			long[] delays = makeDelays(1000, 20, 120);
			printLongArray(delays);

			Scenario sc = new Scenario(authorizedDelay, delays);

			WithCancel c = new WithCancel(authorizedDelay);

			sc.runScenario(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printLongArray(long[] sc) {
		System.out.print("{");
		for (long l : sc) {
			System.out.print(l + ", ");
		}
		System.out.println("}");
	}

	private static long[] makeDelays(int calls, int min, int max) {
		long[] delays = new long[calls];

		for (int i = 0; i < calls; i++) {
			long delay = (int) (Math.random() * (max - min)) + min;
			delays[i] = delay;
		}

		return delays;
	}

}

class Scenario extends Target {
	private final long[] delays;
	private int hits = 0;

	public Scenario(long authorizedDelay, long[] delays) {
		this.delays = delays;
	}

	public void runScenario(Target t) throws InterruptedException {
		print("Start (" + delays.length + ")");
		hits = 0;

		t.setTarget(this);

		for (long delay : delays) {
			Thread.sleep(delay);
			print("Invoke (" + delay + ")");
			t.hit();
		}
		print("Done: (" + hits + ")");
	}

	public void hit() {
		hits++;
		print("Hit");
	}

	public static void print(String key) {
		long nanos = System.nanoTime();
		System.out.println(nanos + " " + key);
	}

}

abstract class Target {
	protected Target target;

	public void setTarget(Target target) {
		this.target = target;
	}

	abstract public void hit();
}

class WithCancel extends Target {

	private Runnable task;
	private ScheduledExecutorService executor;
	private long authorizedDelay;
	private ScheduledFuture<?> future;

	public WithCancel(long authorizedDelay) {
		this.authorizedDelay = authorizedDelay;

		this.executor = Executors.newSingleThreadScheduledExecutor();

		this.task = new Runnable() {
			public void run() {
				target.hit();
			}
		};
	}

	@Override
	public void hit() {
		if (future != null && !future.isDone()) {
			future.cancel(false);
		}

		future = executor.schedule(task, authorizedDelay, TimeUnit.MILLISECONDS);
	}
}
