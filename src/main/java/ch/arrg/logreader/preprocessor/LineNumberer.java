package ch.arrg.logreader.preprocessor;

public class LineNumberer implements AbstractPreProcessor {
	private int counter = 0;

	/** {@inheritDoc} */
	@Override
	public String process(String line) {
		counter++;
		return counter + ":  " + line;
	}

	@Override
	public void reset() {
		counter = 0;
	}
}
