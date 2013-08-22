package ch.arrg.logreader.preprocessor;

public interface AbstractPreProcessor {

	public abstract String process(String line);

	void reset();

}