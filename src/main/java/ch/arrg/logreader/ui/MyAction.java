package ch.arrg.logreader.ui;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public abstract class MyAction extends AbstractAction {
	public MyAction(String name, String command, int mnemonic, String accelerator) {
		super(name);

		if (command != null) {
			putValue(ACTION_COMMAND_KEY, command);
		}

		if (mnemonic != 0) {
			putValue(MNEMONIC_KEY, mnemonic);
		}

		if (command != null) {
			KeyStroke ks = KeyStroke.getKeyStroke(accelerator);
			putValue(ACCELERATOR_KEY, ks);
		}
	}

	public void addToComponent(JComponent comp) {
		String name = (String) getValue(NAME);
		comp.getActionMap().put(name, this);
		comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put((KeyStroke) getValue(ACCELERATOR_KEY), name);
	}

}