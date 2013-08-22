package ch.arrg.logreader.core;

import java.util.ArrayList;
import java.util.List;

import ch.arrg.logreader.interfaces.Consumer;
import ch.arrg.logreader.preprocessor.AbstractPreProcessor;
import ch.arrg.logreader.preprocessor.Beeper;
import ch.arrg.logreader.preprocessor.LineNumberer;
import ch.arrg.logreader.preprocessor.Tabber;
import ch.arrg.logreader.preprocessor.Trimmer;

public class PreProcConsumer implements Consumer {
	private List<AbstractPreProcessor> preProcs = new ArrayList<>();
	private Consumer client;

	public PreProcConsumer(Consumer cons) {
		this.client = cons;

		// TODO CONF 1 preprocs
		if (Config.notificationsEnabled()) {
			preProcs.add(new Beeper());
		}

		preProcs.add(new Trimmer());
		preProcs.add(new Tabber());
		preProcs.add(new LineNumberer());
	}

	@Override
	public synchronized void addLine(String s) {
		for (AbstractPreProcessor pre : preProcs) {
			s = pre.process(s);
		}

		client.addLine(s);
	}

	@Override
	public void clear() {
		for (AbstractPreProcessor pre : preProcs) {
			pre.reset();
		}

		client.clear();
	}
}
