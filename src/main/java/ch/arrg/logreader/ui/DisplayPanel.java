/*
 * Project: ASTRA FA VM
 *
 * Copyright 2012 by Eidgenoessisches Departement fuer Umwelt,
 * Verkehr, Energie und Kommunikation UVEK, Bundesamt fuer
 * Strassen ASTRA, 3003 Bern
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Bundesamt fuer Strassen ASTRA ("Confidential Information").
 * You shall not disclose such "Confidential Information" and
 * shall use it only in accordance with the terms of the license
 * agreement you entered into with ASTRA.
 */
package ch.arrg.logreader.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.ui.logic.MyAction;

class DisplayPanel extends JPanel implements Consumer {
	Logger logger = LoggerFactory.getLogger(DisplayPanel.class);

	JTextPane textArea;
	JScrollPane scroll;
	StyledDocument document;

	public DisplayPanel() {
		this.setLayout(new BorderLayout());

		textArea = new JTextPane();
		textArea.setEditable(false);

		String fontName = Config.getStringProp("ui.console.font.name");
		int fontSize = Config.getIntProp("ui.console.font.size");

		Font f = new Font(fontName, Font.PLAIN, fontSize);
		textArea.setFont(f);
		document = textArea.getStyledDocument();

		// Set default scroll mode (always)
		enableScrolling(true);

		scroll = new JScrollPane(textArea);

		this.add(scroll, BorderLayout.CENTER);
		this.validate();

		// TODO IMPR scroll to bottom when END is pressed
		// ScrollDown action = new ScrollDown();
		// action.addToComponent(this);
	}

	public void enableScrolling(boolean scroll) {
		// DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		// int policy = scroll ? DefaultCaret.ALWAYS_UPDATE :
		// DefaultCaret.NEVER_UPDATE;
		// caret.setUpdatePolicy(policy);
	}

	public void clear() {
		textArea.setText("");
		document = textArea.getStyledDocument();
		repaint();
	}

	public void addLine(String line) {
		try {
			AttributeSet attributes = Styler.makeAttributes(line);
			document.insertString(document.getLength(), line, attributes);
			scrollDown();
			repaint();
		} catch (BadLocationException e) {
			logger.error("Failed to insert text in display panel ! ", e);
		}
	}

	public void scrollDown() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		boolean scrollLock = tk.getLockingKeyState(KeyEvent.VK_SCROLL_LOCK);

		if (!scrollLock) {
			scroll.validate();
			JScrollBar vertical = scroll.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		}
	}

	class ScrollDown extends MyAction {
		ScrollDown() {
			super("Scroll down", "tab-scoll-down", KeyEvent.VK_END, "END");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			scrollDown();
		}
	}
}