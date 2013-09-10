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
package ch.arrg.logreader.filter;

import java.util.HashSet;

// TODO IMPR [GH-4] escape - and + with \
public class WordFilter extends AbstractFilter {
	private HashSet<String> words = new HashSet<>();

	public WordFilter() {

	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return words.isEmpty();
	}

	public void addWord(String word) {
		if (!word.trim().equals("")) {
			words.add(word.toLowerCase());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reset() {
		words.clear();
	}

	/** {@inheritDoc} */
	@Override
	public boolean accepts(String line) {
		if (words.isEmpty())
			return true;

		line = line.toLowerCase();

		for (String word : words) {
			if (word.charAt(0) == '-') {
				// If negative word, line doesn't match if it contains the word
				word = word.substring(1);
				if (word.length() > 0 && line.contains(word)) {
					return false;
				}
			} else {
				if (word.charAt(0) == '+') {
					word = word.substring(1);
				}

				// If positive word, line doesn't match if it doesn't contain
				// the word
				if (!line.contains(word)) {
					return false;
				}
			}
		}

		return true;
	}
}