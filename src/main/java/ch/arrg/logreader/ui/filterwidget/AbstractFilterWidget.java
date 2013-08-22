package ch.arrg.logreader.ui.filterwidget;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.ui.interfaces.FilterCallback;

public abstract class AbstractFilterWidget {
	private static int idGen = 0;

	private static int getNewID() {
		return idGen++;
	}

	public abstract AbstractFilter asFilter();

	private final String id = getClass().getSimpleName() + "[" + getNewID() + "]";

	public String getFilterName() {
		return id;
	}

	public abstract Component asComponent();

	protected void setCallbackKeyListener(JTextField field, final FilterCallback callback) {
		field.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				callback.updateFilter(AbstractFilterWidget.this);
				callback.onKeyReleased(AbstractFilterWidget.this, e);
			}
		});
	}

	public abstract void takeFocus();

	public abstract void setCallback(FilterCallback callback);
}
