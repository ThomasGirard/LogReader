package ch.arrg.logreader.reader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.interfaces.Consumer;

/** Relies on Tailer from apache commons. */
public class CommonsTailReader implements AbstractReader {
	// TODO BUG CommonsReader does not support -n property.
	// private final int TAIL_N = Config.getIntProp("reader.tail.oldLines");
	Logger logger = LoggerFactory.getLogger(CommonsTailReader.class);

	// TODO CONF 3 CommonsTailReader read interval
	private final int READ_INTERVAL = 10;

	private Tailer tailer;
	private String fileName;
	private Adapter adapter;

	public CommonsTailReader(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setConsumer(Consumer cons) {
		this.adapter = new Adapter(cons);
	}

	@Override
	public void start() {
		File f = new File(fileName);
		tailer = Tailer.create(f, adapter, READ_INTERVAL, false);
	}

	@Override
	public void stop() throws IOException {
		tailer.stop();
	}

	/** Adapter from TailerListenerAdapter to Consumer. */
	private class Adapter extends TailerListenerAdapter {
		private Consumer cons;

		public Adapter(Consumer cons) {
			this.cons = cons;
		}

		@Override
		public void handle(String line) {
			try {
				cons.addLine(line + "\n");
			} catch (Exception e) {
				logger.error("Uncaught exception while processing line.", e);
			}
		}
	}

}
