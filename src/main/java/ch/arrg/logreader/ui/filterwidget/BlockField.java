package ch.arrg.logreader.ui.filterwidget;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.filter.BlockFilter;
import ch.arrg.logreader.filter.WordFilter;
import ch.arrg.logreader.ui.UiHelpers;
import ch.arrg.logreader.ui.interfaces.FilterCallback;

public class BlockField extends AbstractFilterWidget {
	private final static String DEFAULT_TO = Config.getStringProp("ui.filters.blockFilter.end");

	private final JTextField from;
	private final JTextField to;

	private final Box box;

	public BlockField() {
		super();

		this.box = new Box(BoxLayout.LINE_AXIS);

		UiHelpers.addFilterLabel(box, "Filter from:");

		from = new JTextField();
		box.add(from);

		UiHelpers.addFilterLabel(box, "and resume at:");

		to = new JTextField(DEFAULT_TO);
		box.add(to);
	}

	@Override
	public BlockFilter asFilter() {

		WordFilter fromF = FilterField.convertTextField(from);
		WordFilter toF = FilterField.convertTextField(to);

		BlockFilter filter = new BlockFilter(fromF, toF);
		return filter;
	}

	@Override
	public Component asComponent() {
		return box;
	}

	@Override
	public void setCallback(FilterCallback callback) {
		setCallbackKeyListener(from, callback);
		setCallbackKeyListener(to, callback);
	}

	@Override
	public void takeFocus() {
		from.requestFocus();
	}
}
