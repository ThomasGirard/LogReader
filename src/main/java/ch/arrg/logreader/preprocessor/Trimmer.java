package ch.arrg.logreader.preprocessor;

import ch.arrg.logreader.core.Config;

public class Trimmer implements AbstractPreProcessor {
	private final String[] trimmables = Config.getStringArrayProp("preproc.trimmer.trimmables");

	/** {@inheritDoc} */
	@Override
	public String process(String line) {
		return removeInfo(line);
	}

	private String removeInfo(String line) {
		for (String toTrim : trimmables) {
			if (line.startsWith(toTrim)) {
				return line.substring(toTrim.length());
			}
		}
		return line;
	}

	@Override
	public void reset() {

	}
}
