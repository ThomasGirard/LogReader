package ch.arrg.logreader.core;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.interfaces.Consumer;

// TODO FEAT load and store filters
public class FilteringConsumer implements Consumer {
	private Consumer consumer;

	private LinkedHashMap<String, AbstractFilter> filters = new LinkedHashMap<>();

	private LinkedList<String> allLines = new LinkedList<>();

	private boolean acceptByDefault = false;

	public void setDefaultBehavior(boolean acceptByDefault) {
		this.acceptByDefault = acceptByDefault;
		refilter();
	}

	public FilteringConsumer() {

	}

	public void setConsumer(Consumer consumer) {
		this.consumer = new PreProcConsumer(consumer);
		// this.consumer = new BufferedConsumer(new PreProcConsumer(consumer));
	}

	@Override
	public synchronized void addLine(String s) {
		allLines.add(s);
		if (consumer != null && accepts(s)) {
			consumer.addLine(s);
		}

		trimBufferSize();
	}

	private void trimBufferSize() {
		int maxLines = Config.getMaxLines();
		if (maxLines != -1 && allLines.size() > maxLines) {
			allLines.removeFirst();
		}
	}

	@Override
	public synchronized void clear() {
		allLines.clear();

		if (consumer != null) {
			consumer.clear();
		}
	}

	/**
	 * @param s
	 * @return
	 */
	private boolean accepts(String line) {
		boolean hasAccepting = false;

		for (AbstractFilter f : filters.values()) {
			if (!f.isEnabled()) {
				continue;
			}

			if (f.isPrintOnAccept()) {
				hasAccepting = true;
			}

			boolean accepts = f.accepts(line);

			if (accepts) {
				if (f.isPrintOnAccept()) {
					return true;
				} else {
					continue;
				}
			} else {
				if (f.isDiscardOnReject()) {
					return false;
				} else {
					continue;
				}
			}
		}

		// When no filter can be accepting, accept (otherwise the display is
		// empty, which is useless)
		if (!hasAccepting) {
			return true;
		} else {
			// When some active filters, use default
			return acceptByDefault;
		}
	}

	public synchronized void removeFilter(String filterName) {
		filters.remove(filterName);
		refilter();
	}

	public synchronized void addFilter(String filterName, AbstractFilter filter) {
		filters.put(filterName, filter);
		refilter();
	}

	public synchronized void updateFilter(String filterName, AbstractFilter filter) {
		filters.put(filterName, filter);
		refilter();
	}

	private synchronized void refilter() {
		if (consumer == null) {
			return;
		}

		consumer.clear();

		for (String s : allLines) {
			if (accepts(s)) {
				consumer.addLine(s);
			}
		}
	}

	public void refresh() {
		refilter();
	}
}