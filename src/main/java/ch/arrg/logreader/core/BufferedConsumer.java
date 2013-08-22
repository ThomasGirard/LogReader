package ch.arrg.logreader.core;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import ch.arrg.logreader.interfaces.Consumer;

@Deprecated
public class BufferedConsumer implements Consumer {
	// TODO CONF 3 BufferedConsumer.Length and maxdelay
	private final int BUFFER_LENGTH = 30;
	private final int BUFFER_MAX_DELAY_MS = 8;

	private Consumer client;

	private ArrayList<String> buffer = new ArrayList<>(BUFFER_LENGTH);

	private long lastPush = 0;
	Semaphore mutex = new Semaphore(1);

	public BufferedConsumer(Consumer cons) {
		this.client = cons;

		Runnable task = new Clearer();
		Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(task, BUFFER_MAX_DELAY_MS,
				BUFFER_MAX_DELAY_MS, TimeUnit.MILLISECONDS);
	}

	@Override
	public void addLine(String line) {
		mutex.acquireUninterruptibly();
		buffer.add(line);

		if (buffer.size() >= BUFFER_LENGTH) {
			outputBuffer();
		}
		mutex.release();
	}

	@Override
	public void clear() {
		// TODO BUG 2 BufferingConsumer.clear not thread safe ?
		buffer.clear();
		client.clear();
	}

	private void outputBuffer() {
		for (String s : buffer) {
			client.addLine(s);
		}

		buffer.clear();
		lastPush = now();
	}

	private long now() {
		return System.currentTimeMillis();
	}

	private class Clearer implements Runnable {
		public void run() {
			if (lastPush + BUFFER_MAX_DELAY_MS > now()) {
				return;
			}

			if (mutex.tryAcquire()) {
				outputBuffer();
				mutex.release();
			}

		}
	}

}
