package ch.arrg.logreader.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.interfaces.ConsumerCallback;

// TODO IMPR 1 ctrl-tab shift-ctrl-tab
public class ConsumerTab implements Consumer {
	// private final static Logger logger = LoggerFactory.getLogger(ConsumerTab.class);

	private DisplayPanel displayPanel;
	private FilterPanel filterPanel;

	private final ConsumerCallback callback;

	private Box box;

	public ConsumerTab(ConsumerCallback callback) {
		this.callback = callback;

		makeComponents();
	}

	public Component asComponent() {
		return box;
	}

	private void makeComponents() {
		box = new Box(BoxLayout.Y_AXIS);
		box.setOpaque(true);
		box.setBackground(new Color(240, 240, 240));

		displayPanel = new DisplayPanel();
		filterPanel = new FilterPanel(callback);

		box.add(filterPanel.getBox());
		box.add(displayPanel);
	}

	/**
	 * @param s
	 */
	@Override
	public void addLine(String s) {
		displayPanel.addLine(s);
	}

	@Override
	public void clear() {
		displayPanel.clear();
	}

	public static void addFilterLabel(Box b, String text) {
		b.add(Box.createHorizontalStrut(10));
		b.add(new JLabel(text));
		b.add(Box.createHorizontalStrut(10));
	}

	public FilterPanel getFilterPanel() {
		return filterPanel;
	}

	public DisplayPanel getDisplayPanel() {
		return displayPanel;
	}
}
