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
import javax.swing.JLabel;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.interfaces.ConsumerCallback;
import ch.arrg.logreader.ui.filterwidget.AbstractFilterWidget;
import ch.arrg.logreader.ui.filterwidget.FilterField;
import ch.arrg.logreader.ui.filterwidget.FilterWidgetDecorator;
import ch.arrg.logreader.ui.interfaces.FilterCallback;

// TODO PERF buffer changes to refilter (do not refilter once per char)
// TODO BUG 3 there's an increasing amount of whitespace after adding new filters

public class FilterPanel implements FilterCallback {
	private Box filterBox;
	private Box outerBox;

	private LinkedList<AbstractFilterWidget> fields = new LinkedList<>();

	private ConsumerCallback callback;

	public FilterPanel(ConsumerCallback callback) {
		this.callback = callback;

		outerBox = new Box(BoxLayout.Y_AXIS);
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

		outerBox.add(inner);
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

	/**
	 * @return
	 */
	public Component getBox() {
		return outerBox;
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
			outerBox.getTopLevelAncestor().validate();
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