package ch.arrg.logreader.preprocessor;

import ch.arrg.logreader.core.Config;

public class Tabber implements AbstractPreProcessor {

	private final String[] indenters = Config.getStringArrayProp("preproc.tabber.indenters");
	private final String[] dedenters = Config.getStringArrayProp("preproc.tabber.dedenters");
	private final String[] reseters = Config.getStringArrayProp("preproc.tabber.reseters");

	private String tabs = "";
	private String tab = "\t";

	@Override
	public String process(String line) {
		updateTabs(line);
		line = tabs + line;
		return line;
	}

	private void updateTabs(String line) {
		for (String indenter : indenters) {
			if (line.startsWith(indenter)) {
				tabs += tab;
				return;
			}
		}

		if (tabs.length() >= tab.length()) {
			for (String dedenter : dedenters) {
				if (line.startsWith(dedenter)) {
					tabs = tabs.substring(tab.length());
					return;
				}
			}
		}

		for (String reseter : reseters) {
			if (line.startsWith(reseter)) {
				reset();
				return;
			}
		}
	}

	@Override
	public void reset() {
		tabs = "";
	}
}
