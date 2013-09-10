package ch.arrg.logreader.core;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.logreader.preprocessor.AbstractPreProcessor;
import ch.arrg.logreader.preprocessor.Beeper;
import ch.arrg.logreader.preprocessor.Tabber;
import ch.arrg.logreader.preprocessor.Trimmer;

/** A Consumer that modifies all lines that pass through. */
public class PreProcessor {
	private List<AbstractPreProcessor> preProcs = new ArrayList<>();

	public PreProcessor() {
		// TODO CONF 2 configure what preprocessors are available ?
		if (Config.notificationsEnabled()) {
			preProcs.add(new Beeper());
		}

		preProcs.add(new Trimmer());
		preProcs.add(new Tabber());
		// preProcs.add(new LineNumberer());
	}

	public synchronized String addLine(String s) {
		for (AbstractPreProcessor pre : preProcs) {
			s = pre.process(s);
		}

		return s;
	}

	public void clear() {
		for (AbstractPreProcessor pre : preProcs) {
			pre.reset();
		}
	}
}
