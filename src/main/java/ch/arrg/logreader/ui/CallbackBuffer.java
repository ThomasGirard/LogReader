package ch.arrg.logreader.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;

// TODO BUG 2 enable debug logging in conf

/**
 * This class buffers calls to the underlying ConsumerCallback so that not too
 * many changes happen. <br>
 * <br>
 * For example, when the user types in a filter, a new event is triggered for
 * each letter typed. This class will buffer the events and only let one go
 * through. This will avoid doing one refilter() operation per key typed. <br>
 * <br>
 * Not all calls are buffered.
 */
public class CallbackBuffer implements FilterConsumerCallback {
	private final static Logger logger = LoggerFactory.getLogger(CallbackBuffer.class);

	private static final long DELAY = 250;

	private FilterConsumerCallback target;

	// Executor for delayed tasks
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	// ScheduledFutures for each pending task, which may be canceled.
	private Map<String, ScheduledFuture<?>> pendingTasks;

	CallbackBuffer(FilterConsumerCallback target) {
		this.target = target;
		pendingTasks = new HashMap<>(2);
	}

	private void reSchedule(long delay, String key, Runnable task) {
		// Retrieve the previous task and cancel it if possible
		ScheduledFuture<?> previous = pendingTasks.get(key);
		if (previous != null && !previous.isDone()) {
			previous.cancel(false);
		}

		// Schedule a new one
		ScheduledFuture<?> newer = executor.schedule(task, delay, TimeUnit.MILLISECONDS);
		pendingTasks.put(key, newer);
	}

	// Buffered calls

	@Override
	public void refresh() {
		logger.info("BC.refresh");

		Runnable r = new Runnable() {
			public void run() {
				target.refresh();
			}
		};

		reSchedule(DELAY, "refresh", r);
	}

	@Override
	public void updateFilter(final String filterName, final AbstractFilter filter) {
		logger.info("BC.updateFilter");

		Runnable r = new Runnable() {
			public void run() {
				target.updateFilter(filterName, filter);
			}
		};

		reSchedule(DELAY, "updateFilter", r);
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

}
