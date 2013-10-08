package ch.arrg.logreader.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.interfaces.Consumer;

/** Spawns a tail -f process. */
public class TailReader implements AbstractReader {
	private final int TAIL_N = Config.getIntProp("reader.tail.oldLines");

	private final static Logger logger = LoggerFactory.getLogger(TailReader.class);

	private final static int READ_INTERVAL_MS = 10;
	private final static int READ_PAUSE_MS = 0;

	private ReadTask task;
	private ScheduledFuture<?> future;
	private Consumer consumer;

	public TailReader(String fileName) throws IOException {
		this.task = new ReadTask(fileName);
	}

	@Override
	public void setConsumer(Consumer cons) {
		this.consumer = cons;
	}

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
	public void stop() throws IOException {
		future.cancel(true);
		task.stop();
	}

	private class ReadTask implements Runnable {
		private final BufferedReader reader;
		private final Process proc;

		public ReadTask(String fileName) throws IOException {
			Runtime r = Runtime.getRuntime();
			String cmd = "tail --retry -n " + TAIL_N + " -f " + fileName;
			proc = r.exec(cmd);
			this.reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		}

		@SuppressWarnings("unused")
		@Override
		public void run() {
			try {
				String line = null;
				while ((line = reader.readLine()) != null) {
					consumer.addLine(line + "\n");

					if (READ_PAUSE_MS > 0) {
						Thread.sleep(READ_PAUSE_MS);
					}
				}
			} catch (IOException | InterruptedException e) {
				logger.warn("Exception in TailReader task.", e);
			}
		}

		public void stop() throws IOException {
			proc.destroy();
			reader.close();
		}
	}

}
