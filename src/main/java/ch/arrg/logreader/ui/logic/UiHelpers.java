package ch.arrg.logreader.ui.logic;

import javax.swing.Box;
import javax.swing.JLabel;

public class UiHelpers {

	public static void addFilterLabel(Box b, String text) {
		b.add(Box.createHorizontalStrut(10));
		b.add(new JLabel(text));
		b.add(Box.createHorizontalStrut(10));
	}

}
