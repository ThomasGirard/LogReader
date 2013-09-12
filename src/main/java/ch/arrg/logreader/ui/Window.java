package ch.arrg.logreader.ui;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Bridge;
import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.core.Resources;
import ch.arrg.logreader.interfaces.AppCallback;
import ch.arrg.logreader.interfaces.FilterConsumerCallback;
import ch.arrg.logreader.interfaces.HasBridges;
import ch.arrg.logreader.ui.filterwidget.impl.BlockField;
import ch.arrg.logreader.ui.filterwidget.impl.FilterField;
import ch.arrg.logreader.ui.filterwidget.impl.TabAwareField;
import ch.arrg.logreader.ui.logic.MyAction;

public class Window extends JFrame implements HasBridges {
	private final static Logger logger = LoggerFactory.getLogger(Window.class);

	private LinkedList<String> names = new LinkedList<>();
	private LinkedList<FilterConsumerCallback> callbacks = new LinkedList<>();
	private LinkedList<ConsumerTab> tabs = new LinkedList<>();
	private JTabbedPane tabPanel;

	private final AppCallback appCallback;

	private Actions actions = new Actions();

	public Window(AppCallback callback) {
		this.appCallback = callback;

		setLookAndFeel();

		makeMenu();
		makeComponents();
		setProperties();
		setListeners();
		setupTabTraversalKeys(tabPanel);
	}

	private void setLookAndFeel() {
		String lookAndFeelName = Config.getStringProp("ui.lookAndFeel");
		if (lookAndFeelName != null) {
			try {
				if (lookAndFeelName.equals("SYSTEM")) {
					lookAndFeelName = UIManager.getSystemLookAndFeelClassName();
				} else if (lookAndFeelName.equals("CROSS")) {
					lookAndFeelName = UIManager.getCrossPlatformLookAndFeelClassName();
				}

				UIManager.setLookAndFeel(lookAndFeelName);
			} catch (Exception e) {
				// handle exception
				logger.warn("Unable to set desired L&F ({}), falling back to default.", lookAndFeelName);

				List<String> names = new ArrayList<>();
				for (LookAndFeelInfo v : UIManager.getInstalledLookAndFeels()) {
					names.add(v.getClassName());
				}
				logger.info("Supported look and feels: {}", names);
			}
		}
	}

	@Override
	public void start() {
		logger.info("Making UI visible.");
		setVisible(true);
	}

	private int tabIndex() {
		return tabPanel.getSelectedIndex();
	}

	private FilterConsumerCallback getCurrentTabCallback() {
		return callbacks.get(tabIndex());
	}

	private FilterPanel getCurrentFilterPanel() {
		return tabs.get(tabIndex()).getFilterPanel();
	}

	private void makeComponents() {
		tabPanel = new JTabbedPane();

		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(tabPanel);

		add(b);

		makeMenu();
	}

	private JMenuBar makeMenu() {
		JMenuBar bar = new JMenuBar();

		bar.add(makeMainMenu());
		bar.add(makeTabMenu());
		//bar.add(makeOptionMenu());
		bar.add(makeFilterMenu());

		this.setJMenuBar(bar);

		return bar;
	}

	private void setProperties() {
		this.setTitle("Log Reader");
		// TODO CONF 2 default window size and position
		this.setSize(new Dimension(1200, 600));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.validate();

		// Set window icon
		try {
			InputStream f = Resources.getWindowIcon();
			Image img = ImageIO.read(f);
			this.setIconImage(img);
		} catch (IOException e) {
			logger.warn("IOException while reading window icon.", e);
		}
	}

	private void setListeners() {
		// On quit
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Window.this.dispose();
				appCallback.quit();
			}
		});

		// Change window title when changing tab
		tabPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				String name = names.get(tabIndex());
				Window.this.setTitle(name);
			}
		});

	}

	private void openFileDialog() {
		JFileChooser chooser = new JFileChooser();
		int code = chooser.showOpenDialog(this);
		if (code == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			String fName = selected.getName();
			String fPath = selected.getPath();

			try {
				appCallback.openFile(fName, fPath);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could not open file.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void addBridge(String name, Bridge bridge) {
		ConsumerTab tab = new ConsumerTab(bridge.getCallback());
		bridge.setConsumer(tab);

		names.add(name);
		callbacks.add(bridge.getCallback());
		tabs.add(tab);

		Component tabContent = tab.asComponent();
		tabPanel.addTab(name, tabContent);
		tabPanel.setSelectedComponent(tabContent);

		remakeHotKeys();
	}

	@Override
	public void removeBridge(String name, Bridge bridge) {
		int idx = callbacks.indexOf(bridge);
		tabPanel.removeTabAt(idx);

		names.remove(idx);
		callbacks.remove(idx);
		tabs.remove(idx);

		remakeHotKeys();
	}

	/** Bind the shortcuts alt-1 ... 9 to the tabs */
	private void remakeHotKeys() {
		int maxKeys = tabPanel.getTabCount();
		if (maxKeys > 9) {
			maxKeys = 9;
		}

		for (int i = 0; i < maxKeys; i++) {
			tabPanel.setMnemonicAt(i, KeyEvent.VK_1 + i);
		}
	}

	private static void setupTabTraversalKeys(JTabbedPane tabbedPane) {
		// Taken from 
		// http://www.davidc.net/programming/java/how-make-ctrl-tab-switch-tabs-jtabbedpane

		KeyStroke ctrlTab = KeyStroke.getKeyStroke("ctrl TAB");
		KeyStroke ctrlShiftTab = KeyStroke.getKeyStroke("ctrl shift TAB");

		// Remove ctrl-tab from normal focus traversal
		Set<AWTKeyStroke> forwardKeys = new HashSet<AWTKeyStroke>(
				tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.remove(ctrlTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

		// Remove ctrl-shift-tab from normal focus traversal
		Set<AWTKeyStroke> backwardKeys = new HashSet<AWTKeyStroke>(
				tabbedPane.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.remove(ctrlShiftTab);
		tabbedPane.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

		// Add keys to the tab's input map
		InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(ctrlTab, "navigateNext");
		inputMap.put(ctrlShiftTab, "navigatePrevious");
	}

	private JMenu makeMainMenu() {
		JMenu menu = new JMenu("File");

		JMenuItem item;

		item = new JMenuItem(actions.new OpenFile());
		menu.add(item);

		item = new JMenuItem(actions.new ClearAllTabs());
		menu.add(item);

		return menu;
	}

	//	private JMenu makeOptionMenu() {
	//		JMenu menu = new JMenu("Option");
	//
	//		JMenuItem item;
	//
	//		item = new JMenuItem(actions.new ToggleScroll());
	//		menu.add(item);
	//
	//		return menu;
	//	}

	private JMenu makeTabMenu() {
		JMenu menu = new JMenu("Tab");

		JMenuItem item;

		item = new JMenuItem(actions.new ClearTab());
		menu.add(item);

		item = new JMenuItem(actions.new CloseTab());
		menu.add(item);

		return menu;
	}

	private JMenu makeFilterMenu() {
		JMenu menu = new JMenu("Filters");

		JMenuItem item;

		item = new JMenuItem(actions.new NewBasicFilter());
		menu.add(item);

		item = new JMenuItem(actions.new NewBlockFilter());
		menu.add(item);

		item = new JMenuItem(actions.new NewTabAwareFilter());
		menu.add(item);

		return menu;
	}

	private class Actions {
		class NewBlockFilter extends MyAction {
			NewBlockFilter() {
				super("New block filter", "filter-new-block", 0, null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentFilterPanel().addFilter(new BlockField());
			}
		}

		class NewTabAwareFilter extends MyAction {
			NewTabAwareFilter() {
				super("New tab aware filter", "filter-new-tab-aware", 0, null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentFilterPanel().addFilter(new TabAwareField());
			}
		}

		class NewBasicFilter extends MyAction {
			NewBasicFilter() {
				super("New Basic Filter", "filter-new-basic", 0, null);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				getCurrentFilterPanel().addFilter(new FilterField());
			}
		}

		class ClearAllTabs extends MyAction {
			ClearAllTabs() {
				super("Clear all", "file-clear-all", 'd', "control shift D");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for (FilterConsumerCallback cb : callbacks) {
							cb.clear();
						}
					}
				});
			}
		}

		class OpenFile extends MyAction {
			OpenFile() {
				super("Open file", "file-open", KeyEvent.VK_O, "control O");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				openFileDialog();
			}
		}

		class ClearTab extends MyAction {
			ClearTab() {
				super("Clear", "tab-clear", KeyEvent.VK_D, "control D");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Window.this.getCurrentTabCallback().clear();
			}
		}

		class CloseTab extends MyAction {
			CloseTab() {
				super("Close", "tab-close", KeyEvent.VK_W, "control W");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (names.size() > 1) {
					// TODO BUG 3 can't close last tab
					String name = names.get(tabIndex());
					appCallback.removeConsumer(name);
				}
			}
		}
		// End of Actions
	}

}
