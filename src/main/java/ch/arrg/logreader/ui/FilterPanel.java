package ch.arrg.logreader.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;
import ch.arrg.logreader.ui.filterwidget.AbstractFilterWidget;
import ch.arrg.logreader.ui.filterwidget.FilterWidgetDecorator;
import ch.arrg.logreader.ui.filterwidget.impl.FilterField;
import ch.arrg.logreader.ui.interfaces.FilterCallback;

// TODO BUG 3 there's an increasing amount of whitespace after adding new filters
public class FilterPanel implements FilterCallback {
	private final static Logger logger = LoggerFactory.getLogger(FilterPanel.class);

	private Box filterBox;
	private JComponent component;

	private LinkedList<AbstractFilterWidget> fields = new LinkedList<>();

	private FilterConsumerCallback callback;

	public FilterPanel(FilterConsumerCallback callback) {
		this.callback = new FilterDebounce(callback);

		Box outerBox = new Box(BoxLayout.Y_AXIS);
		this.component = outerBox;

		filterBox = new Box(BoxLayout.Y_AXIS);

		outerBox.add(filterBox);
		outerBox.add(Box.createVerticalStrut(5));
		makeControls();
		outerBox.add(Box.createVerticalStrut(5));

		// Add a filter by default
		defaultFilters();
	}

	private void makeControls() {
		Box inner = new Box(BoxLayout.X_AXIS);
		inner.add(Box.createHorizontalGlue());

		inner.setMaximumSize(new Dimension(20000, 22));

		inner.add(new JLabel("Remaining lines: "));
		final JComboBox<DefaultBehavior> defaultBehavior = new JComboBox<DefaultBehavior>(DefaultBehavior.values());
		defaultBehavior.setSelectedItem(DefaultBehavior.DISCARD);

		defaultBehavior.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isDisplay = (defaultBehavior.getSelectedItem() == DefaultBehavior.DISPLAY);
				callback.setDefaultBehavior(isDisplay);
			}
		});
		defaultBehavior.setMaximumSize(defaultBehavior.getPreferredSize());
		inner.add(defaultBehavior);

		component.add(inner);
	}

	private enum DefaultBehavior {
		DISCARD("discard"), DISPLAY("display");

		private String label;

		private DefaultBehavior(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	@Override
	public void onKeyReleased(AbstractFilterWidget widget, KeyEvent e) {
		int code = e.getKeyCode();

		if (widget != null) {
			switch (code) {
			case KeyEvent.VK_ESCAPE:
			case KeyEvent.VK_DELETE:
				removeFilter(widget);
				break;
			case KeyEvent.VK_INSERT:
			case KeyEvent.VK_ENTER:
				addFilter(new FilterField());
				break;
			}
		}
	}

	@Override
	public void updateFilter(AbstractFilterWidget field) {
		callback.updateFilter(field.getFilterName(), field.asFilter());
	}

	@Override
	public void moveUp(AbstractFilterWidget widget) {
		if (fields.size() < 2) {
			return;
		}

		// Find the index of the widget, and the index of the previous one
		int idxDown = fields.indexOf(widget);
		int idxUp = idxDown - 1;

		// Can't move up when at the top
		if (idxDown == 0) {
			return;
		}

		swapFilters(idxDown, idxUp);
	}

	@Override
	public void moveDown(AbstractFilterWidget widget) {
		// Find the index of the widget
		int idxUp = fields.indexOf(widget);

		// moveDown is the same as moving the next filter up
		if (idxUp + 1 < fields.size()) {
			AbstractFilterWidget next = fields.get(idxUp + 1);
			moveUp(next);
		}
	}

	private void swapFilters(int idxLow, int idxHigh) {
		if (idxHigh == idxLow) {
			return;
		} else if (idxHigh < idxLow) {
			swapFilters(idxHigh, idxLow);
			return;
		}

		assert idxHigh > idxLow;

		// Swap fields
		AbstractFilterWidget wLow = fields.get(idxLow);
		AbstractFilterWidget wHigh = fields.get(idxHigh);

		fields.set(idxLow, wHigh);
		fields.set(idxHigh, wLow);

		// Swap decorators
		Component deco1 = filterBox.getComponent(idxLow);
		Component deco2 = filterBox.getComponent(idxHigh);

		filterBox.remove(deco1);
		filterBox.remove(deco2);
		filterBox.add(deco2, idxLow);
		filterBox.add(deco1, idxHigh);

		filterBox.validate();

		// Swap filters in logic
		callback.swapFilters(wLow.getFilterName(), wHigh.getFilterName());
	}

	/**
	 * @return
	 */
	public Component getBox() {
		return component;
	}

	public void removeFilter(AbstractFilterWidget f) {
		if (f == null) {
			return;
		}

		// Find previous filter and give it focus
		int fIdx = fields.indexOf(f);
		if (fIdx > 0) {
			fields.get(fIdx - 1).takeFocus();
		}

		// Remove from data structures
		fields.remove(f);

		// Remove from hierarchy
		filterBox.remove(fIdx);
		filterBox.getTopLevelAncestor().validate();

		// Notify callback
		callback.removeFilter(f.getFilterName());
	}

	FilterWidgetDecorator addFilter(AbstractFilterWidget widget) {
		// Register field
		fields.add(widget);

		// Decorate
		FilterWidgetDecorator deco = new FilterWidgetDecorator(widget);
		deco.setCallback(this);

		// Add component to hierarchy
		filterBox.add(deco.asComponent());
		Container top = filterBox.getTopLevelAncestor();
		if (top != null) {
			component.getTopLevelAncestor().validate();
		}
		deco.takeFocus();

		// Add filter to callback
		callback.addFilter(deco.getFilterName(), deco.asFilter());

		return deco;
	}

	private void defaultFilters() {
		String defaultFilter = Config.getStringProp("ui.filters.defaultFilter");
		FilterField ff = new FilterField();
		ff.setText(defaultFilter);

		FilterWidgetDecorator deco = addFilter(ff);
		deco.setIsPrintOnAccept(false);
	}
}