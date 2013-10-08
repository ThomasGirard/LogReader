package ch.arrg.logreader.ui.filterwidget.impl;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import ch.arrg.logreader.filter.WordFilter;
import ch.arrg.logreader.ui.filterwidget.AbstractFilterWidget;
import ch.arrg.logreader.ui.filterwidget.UIOnlyWidget;
import ch.arrg.logreader.ui.interfaces.FilterCallback;
import ch.arrg.logreader.ui.logic.UiHelpers;

// TODO FEAT Highlight widget: there's currently now way to callback from this

public class HighlightFilterWidget extends AbstractFilterWidget implements UIOnlyWidget {

	private JTextField textField;
	private Box box;

	public HighlightFilterWidget() {
		super();

		box = new Box(BoxLayout.LINE_AXIS);

		UiHelpers.addFilterLabel(box, "Highlight: ");

		textField = new JTextField();
		box.add(textField);
	}

	@Override
	public Component asComponent() {
		return box;
	}

	@Override
	public WordFilter asFilter() {
		return null;
	}

	public static WordFilter convertTextField(JTextField field) {
		String text = field.getText();

		WordFilter filter = new WordFilter();

		String[] whiteList = text.split("\\s+");
		for (String word : whiteList) {
			if (word.trim().length() > 0) {
				filter.addWord(word);
			}
		}

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
