package ch.arrg.logreader.ui.filterwidget;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import ch.arrg.logreader.filter.TabAwareFilter;
import ch.arrg.logreader.filter.WordFilter;
import ch.arrg.logreader.ui.interfaces.FilterCallback;
import ch.arrg.logreader.ui.logic.UiHelpers;

public class TabAwareField extends AbstractFilterWidget {

	private JTextField textField;
	private Box box;

	public TabAwareField() {
		super();

		box = new Box(BoxLayout.LINE_AXIS);

		UiHelpers.addFilterLabel(box, "Reject from:");

		textField = new JTextField();
		box.add(textField);

		UiHelpers.addFilterLabel(box, "including following tabbed lines.");
	}

	@Override
	public Component asComponent() {
		return box;
	}

	@Override
	public TabAwareFilter asFilter() {
		WordFilter inner = FilterField.convertTextField(textField);
		TabAwareFilter filter = new TabAwareFilter(inner);
		return filter;
	}

	@Override
	public void setCallback(final FilterCallback callback) {
		setCallbackKeyListener(textField, callback);
	}

	@Override
	public void takeFocus() {
		textField.requestFocus();
	}

	public void setText(String text) {
		textField.setText(text);
	}
}
