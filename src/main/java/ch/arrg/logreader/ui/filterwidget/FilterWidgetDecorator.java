package ch.arrg.logreader.ui.filterwidget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import ch.arrg.logreader.filter.AbstractFilter;
import ch.arrg.logreader.ui.interfaces.FilterCallback;
import ch.arrg.logreader.ui.logic.UiHelpers;

// TODO FEAT make filters orderable

public class FilterWidgetDecorator extends AbstractFilterWidget {

	private final AbstractFilterWidget widget;
	private final Box box;

	private JCheckBox enabledCheckbox;
	private JComboBox<SelectEnum> behaviorSelect;

	private FilterCallback callback;

	public FilterWidgetDecorator(AbstractFilterWidget widget) {
		this.widget = widget;
		this.box = new Box(BoxLayout.LINE_AXIS);
		Dimension dim = new Dimension(20000, 24);
		box.setMaximumSize(dim);
		box.setPreferredSize(dim);
		box.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));

		addCheckbox();
		box.add(widget.asComponent());

		UiHelpers.addFilterLabel(box, "and");

		addSelect();
	}

	enum SelectEnum {
		PRINT_ON_ACCEPT("print matching lines"), DISCARD_ON_REJECT("discard rejected lines");

		private final String name;

		private SelectEnum(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	private void addSelect() {
		behaviorSelect = new JComboBox<>(SelectEnum.values());
		behaviorSelect.setSelectedIndex(0);
		behaviorSelect.setMaximumSize(behaviorSelect.getPreferredSize());

		behaviorSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rebuild();
			}
		});

		box.add(behaviorSelect);
	}

	private void addCheckbox() {

		enabledCheckbox = new JCheckBox();
		enabledCheckbox.setSelected(true);
		enabledCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Not a call to rebuild because since the checkbox is disabled it wouldn't work.
				callback.updateFilter(FilterWidgetDecorator.this);
			}
		});

		box.add(enabledCheckbox);
	}

	@Override
	public Component asComponent() {
		return box;
	}

	@Override
	public AbstractFilter asFilter() {
		AbstractFilter filter = widget.asFilter();

		filter.setEnabled(enabledCheckbox.isSelected());

		SelectEnum val = (SelectEnum) (behaviorSelect.getSelectedItem());
		filter.setIsPrintOnAccept(val == SelectEnum.PRINT_ON_ACCEPT);

		return filter;
	}

	@Override
	public void setCallback(FilterCallback callback) {
		this.callback = callback;
		widget.setCallback(new DecoratorCallback());
	}

	@Override
	public void takeFocus() {
		widget.takeFocus();
	}

	protected void rebuild() {
		if (enabledCheckbox.isSelected()) {
			callback.updateFilter(this);
		}
	}

	@Override
	public String getFilterName() {
		return widget.getFilterName();
	}

	class DecoratorCallback implements FilterCallback {
		@Override
		public void updateFilter(AbstractFilterWidget widget) {
			rebuild();
		}

		@Override
		public void onKeyReleased(AbstractFilterWidget widget, KeyEvent e) {
			callback.onKeyReleased(widget, e);
		}
	}

	public void setIsPrintOnAccept(boolean isPrintOnAccept) {
		SelectEnum val = isPrintOnAccept ? SelectEnum.PRINT_ON_ACCEPT : SelectEnum.DISCARD_ON_REJECT;
		behaviorSelect.setSelectedItem(val);
	}

}
