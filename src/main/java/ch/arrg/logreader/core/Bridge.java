package ch.arrg.logreader.core;

import java.io.IOException;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.interfaces.ConsumerCallback;
import ch.arrg.logreader.reader.AbstractReader;

/**
 * A bridge connects a StreamReader to a Consumer, adding a FilteringConsumer in
 * the middle.
 */
public class Bridge implements ConsumerCallback {

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

	@Override
	public void removeFilter(String filterName) {
		filterer.removeFilter(filterName);
	}

	/** {@inheritDoc} */
	@Override
	public void addFilter(String filterName, AbstractFilter filter) {
		filterer.addFilter(filterName, filter);
	}

	@Override
	public void updateFilter(String filterName, AbstractFilter filter) {
		filterer.updateFilter(filterName, filter);
	}

	@Override
	public void clearConsole() {
		filterer.clear();
	}

	@Override
	public void refresh() {
		filterer.refresh();
	}

	@Override
	public void setDefaultBehavior(boolean isDisplay) {
		filterer.setDefaultBehavior(isDisplay);
	}
}
