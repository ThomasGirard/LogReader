package ch.arrg.logreader.ui;

import ch.arrg.jdebounce.DebouncerStore;
import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;

/**
 * This class debounces calls to the underlying ConsumerCallback so that not too
 * many changes happen. <br>
 */
public class FilterDebounce implements FilterConsumerCallback {
	private static final long DELAY = 250;

	private FilterConsumerCallback target;
	private DebouncerStore<String> debouncers = new DebouncerStore<>();

	FilterDebounce(FilterConsumerCallback target) {
		this.target = target;
	}

	// Buffered calls

	@Override
	public void refresh() {

		Runnable r = new Runnable() {
			public void run() {
				target.refresh();
			}
		};

		debouncers.registerOrGetDebouncer("refresh").debounce(DELAY, r);
	}

	@Override
	public void updateFilter(final String filterName, final AbstractFilter filter) {

		Runnable r = new Runnable() {
			public void run() {
				target.updateFilter(filterName, filter);
			}
		};

		debouncers.registerOrGetDebouncer("updateFilter").debounce(DELAY, r);
	}

	// Unbuffered calls 
	@Override
	public void clear() {
		target.clear();
	}

	@Override
	public void removeFilter(String filterName) {
		target.removeFilter(filterName);
	}

	@Override
	public void addFilter(String filterName, AbstractFilter filter) {
		target.addFilter(filterName, filter);
	}

	@Override
	public void setDefaultBehavior(boolean isDisplay) {
		target.setDefaultBehavior(isDisplay);
	}

	@Override
	public void swapFilters(String filterName1, String filterName2) {
		target.swapFilters(filterName1, filterName2);
	}

}
