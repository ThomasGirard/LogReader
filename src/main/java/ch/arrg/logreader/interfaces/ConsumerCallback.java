package ch.arrg.logreader.interfaces;

import ch.arrg.logreader.filter.AbstractFilter;

/**
 * TODO document
 * */
public interface ConsumerCallback {

	public void clearConsole();

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
