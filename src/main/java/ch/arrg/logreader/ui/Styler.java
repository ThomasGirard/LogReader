package ch.arrg.logreader.ui;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Styler {
	private final static Color COLOR_ERROR = new Color(255, 30, 30);
	private final static Color COLOR_WARN = new Color(90, 20, 20);
	private final static Color COLOR_DEBUG = new Color(100, 100, 100);

	static AttributeSet makeAttributes(String line) {
		SimpleAttributeSet att = new SimpleAttributeSet();

		if (line.contains("WARN")) {
			StyleConstants.setForeground(att, COLOR_WARN);
		} else if (line.contains("DEBUG")) {
			StyleConstants.setForeground(att, COLOR_DEBUG);
		} else if (line.contains("ERROR")) {
			StyleConstants.setForeground(att, COLOR_ERROR);
		}

		return att;
	}
}
