package ch.arrg.logreader.interfaces;

import ch.arrg.logreader.filter.AbstractFilter;

/**
 * Callback to the FilteringConsumer for the UI.
 * */
public interface FilterConsumerCallback {

	public void clear();

	/**
	 * @param filters
	 */
	void removeFilter(String filterName);

	/**
	 * @param filterName
	 * @param filter
	 */
	void addFilter(String filterName, AbstractFilter filter);

	void refresh();

	void updateFilter(String filterName, AbstractFilter filter);

	public void setDefaultBehavior(boolean isDisplay);
}
