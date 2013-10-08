package ch.arrg.logreader.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;

/**
 * A consumer that also have filters. A call to addLine will only be forwarded
 * if the filters match.
 * 
 * Lines are stored here.
 */
public class FilteringConsumer implements Consumer, FilterConsumerCallback {
	private final static Logger logger = LoggerFactory.getLogger(FilteringConsumer.class);

	private Consumer consumer;

	private PreProcessor preProc = new PreProcessor();

	private List<String> filterNames = new ArrayList<>();
	private List<AbstractFilter> filters = new ArrayList<>();

	private LinkedList<String> allLines = new LinkedList<>();

	private boolean acceptByDefault = false;

	public void setDefaultBehavior(boolean acceptByDefault) {
		this.acceptByDefault = acceptByDefault;
		refilter();
	}

	public FilteringConsumer() {

	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

	@Override
	public synchronized void addLine(String s) {
		String processed = preProc.addLine(s);

		allLines.add(processed);
		if (accepts(processed)) {
			consumer.addLine(processed);
		}

		trimBufferSize();
	}

	// TODO BUG 2 with -n, old lines are removed only on refilter()
	private void trimBufferSize() {
		int maxLines = Config.getMaxLines();
		if (maxLines != -1 && allLines.size() > maxLines) {
			allLines.removeFirst();
		}
	}

	@Override
	public synchronized void clear() {
		allLines.clear();
		preProc.clear();

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

		for (AbstractFilter f : filters) {
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
		filterNames.add(filterName);
		filters.add(filter);
		refilter();
	}

	public synchronized void updateFilter(String filterName, AbstractFilter filter) {
		int idx = filterNames.indexOf(filterName);
		filters.set(idx, filter);

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

	@Override
	public void swapFilters(String filterName1, String filterName2) {
		int idx1 = filterNames.indexOf(filterName1);
		int idx2 = filterNames.indexOf(filterName2);

		AbstractFilter f1 = filters.get(idx1);
		AbstractFilter f2 = filters.get(idx2);

		filters.set(idx1, f2);
		filters.set(idx2, f1);

		refilter();
	}
}
