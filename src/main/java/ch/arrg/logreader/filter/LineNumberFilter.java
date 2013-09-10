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

/** Unused */
public class LineNumberFilter extends AbstractFilter {
	private int start = 0;
	private int stop = Integer.MAX_VALUE;

	@Override
	public void reset() {
		start = 0;
		stop = Integer.MAX_VALUE;
	}

	@Override
	public boolean accepts(String line) {
		int firstTab = line.indexOf('\t');
		if (firstTab < 0) {
			return true;
		}

		int no = Integer.parseInt(line.substring(0, firstTab));

		return no >= start && no < stop;
	}
}
