package ch.arrg.logreader.ui.interfaces;

import java.awt.event.KeyEvent;

import ch.arrg.logreader.ui.filterwidget.AbstractFilterWidget;


public interface FilterCallback {
	public void updateFilter(AbstractFilterWidget widget);

	public void onKeyReleased(AbstractFilterWidget widget, KeyEvent e);
}
