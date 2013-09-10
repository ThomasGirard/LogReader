package ch.arrg.logreader.preprocessor;

import ch.arrg.logreader.core.Config;

public class Tabber implements AbstractPreProcessor {

	private final String[] indenters = Config.getStringArrayProp("preproc.tabber.indenters");
	private final String[] dedenters = Config.getStringArrayProp("preproc.tabber.dedenters");
	private final String[] reseters = Config.getStringArrayProp("preproc.tabber.reseters");

	private String tabs = "";
	private final static String TAB_CHAR = "\t";

	@Override
	public String process(String line) {
		// Reset if necessary
		testReset(line);
		// Indent as needed
		line = tabs + line;
		// Update indentation
		updateTabs(line);

		return line;
	}

	private void testReset(String line) {
		boolean hasTabs = tabs.length() != 0;
		if (hasTabs) {
			// Reset on match
			for (String reseter : reseters) {
				if (line.startsWith(reseter)) {
					reset();
					return;
				}
			}
		}
	}

	private void updateTabs(String line) {
		boolean hasTabs = tabs.length() != 0;
		if (hasTabs) {
			// Dedent on match
			for (String dedenter : dedenters) {
				if (line.startsWith(dedenter)) {
					tabs = tabs.substring(TAB_CHAR.length());
					return;
				}
			}
		}

		// Indent on match
		for (String indenter : indenters) {
			if (line.startsWith(indenter)) {
				tabs += TAB_CHAR;
				return;
			}
		}

	}

	@Override
	public void reset() {
		tabs = "";
	}
}
