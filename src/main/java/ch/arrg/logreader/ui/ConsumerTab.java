package ch.arrg.logreader.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;
import ch.arrg.logreader.ui.logic.MyAction;

public class ConsumerTab implements Consumer {
	// private final static Logger logger = LoggerFactory.getLogger(ConsumerTab.class);

	private JScrollPane scrollPanel;
	private DisplayPanel displayPanel;
	private FilterPanel filterPanel;

	private final FilterConsumerCallback callback;

	private Box box;

	public ConsumerTab(FilterConsumerCallback callback) {
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

		filterPanel = new FilterPanel(callback);
		box.add(filterPanel.getBox());

		displayPanel = new DisplayPanel();
		scrollPanel = new JScrollPane(displayPanel.getComponent());
		// TODO LOW seems a bit hackish: we have to do this because otherwise scrolling is very slow in panel.
		scrollPanel.getVerticalScrollBar().setUnitIncrement(18);
		box.add(scrollPanel);

		ScrollDown action = new ScrollDown();
		action.addToComponent(box);
	}

	/**
	 * @param s
	 */
	@Override
	public void addLine(String s) {
		displayPanel.addLine(s);
		scrollDown();
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

	// TODO IMPR make scroll locking work with menu
	public void scrollDown() {
		if (!scrollPanel.isValid()) {
			// Needed because otherwise the app will refilter too early on startup
			return;
		}

		Toolkit tk = Toolkit.getDefaultToolkit();
		boolean scrollLock = tk.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);

		if (!scrollLock) {
			scrollPanel.validate();
			JScrollBar vertical = scrollPanel.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		}
	}

	class ScrollDown extends MyAction {
		ScrollDown() {
			super("Scroll down", "tab-scoll-down", KeyEvent.VK_END, "END");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("Scroll down ?");
			scrollDown();
		}
	}
}
