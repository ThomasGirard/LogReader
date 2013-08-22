package ch.arrg.logreader.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.interfaces.Consumer;

/**
 * This class reads from an inputstream periodically and sends full lines to a
 * Consumer
 * 
 */
public class StreamReader implements AbstractReader {
	private final static Logger logger = LoggerFactory.getLogger(StreamReader.class);

	// TODO CONF 3 StreamReader read interval
	private final static int READ_INTERVAL_MS = 10;

	private ReadTask task;
	private ScheduledFuture<?> future;
	private Consumer consumer;

	public StreamReader(InputStream is) {
		this.task = new ReadTask(is);
	}

	@Override
	public void setConsumer(Consumer cons) {
		this.consumer = cons;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logreader.Reader#start()
	 */
	@Override
	public void start() {
		consumer.clear();
		future = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(task, 0, READ_INTERVAL_MS,
				TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logreader.Reader#stop()
	 */
	@Override
	public void stop() {
		future.cancel(true);
		task.stop();
	}

	private class ReadTask implements Runnable {
		private final BufferedReader reader;

		public ReadTask(InputStream is) {
			this.reader = new BufferedReader(new InputStreamReader(is));
		}

		@Override
		public void run() {
			try {
				// TODO BUG 3 reopen file if it closes in StreamReader
				String line = null;
				while ((line = reader.readLine()) != null) {
					consumer.addLine(line + "\n");
				}
			} catch (IOException e) {
				logger.warn("Exception in TailReader task.", e);
			}
		}

		public void stop() {
			// TODO BUG 3 close StreamReader on quit
			// scanner.close();
		}
	}
}
