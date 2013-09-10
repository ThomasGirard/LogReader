package ch.arrg.logreader.core;

import java.io.IOException;

import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;
import ch.arrg.logreader.reader.AbstractReader;

/**
 * A bridge connects a StreamReader to a Consumer, adding a FilteringConsumer in
 * the middle.
 */
public class Bridge {

	private final AbstractReader reader;
	private final FilteringConsumer filterer;

	public Bridge(AbstractReader input) {
		this.reader = input;
		this.filterer = new FilteringConsumer();
		reader.setConsumer(filterer);
	}

	public void setConsumer(Consumer cons) {
		filterer.setConsumer(cons);
	}

	public void start() {
		reader.start();
	}

	public void stop() throws IOException {
		reader.stop();
	}

	public FilterConsumerCallback getCallback() {
		return filterer;
	}

}
