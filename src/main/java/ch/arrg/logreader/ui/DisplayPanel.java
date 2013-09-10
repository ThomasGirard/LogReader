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

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.interfaces.Consumer;

class DisplayPanel implements Consumer {
	private Logger logger = LoggerFactory.getLogger(DisplayPanel.class);

	private JTextPane textArea;
	private StyledDocument document;

	public DisplayPanel() {
		textArea = new JTextPane();
		textArea.setEditable(false);

		// Set font
		String fontName = Config.getStringProp("ui.console.font.name");
		int fontSize = Config.getIntProp("ui.console.font.size");
		Font f = new Font(fontName, Font.PLAIN, fontSize);
		textArea.setFont(f);

		// Retrieve document
		document = textArea.getStyledDocument();
	}

	public void clear() {
		textArea.setText("");
		document = textArea.getStyledDocument();
		textArea.repaint();
	}

	public void addLine(String line) {
		try {
			AttributeSet attributes = Styler.makeAttributes(line);
			document.insertString(document.getLength(), line, attributes);
			textArea.repaint();
		} catch (BadLocationException e) {
			logger.error("Failed to insert text in display panel ! ", e);
		}
	}

	public Component getComponent() {
		return textArea;
	}
}