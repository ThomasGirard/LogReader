package ch.arrg.logreader.interfaces;

/** Interface for a type that is able to consume text line by line. */
public interface Consumer {
	public void addLine(String line);

	public void clear();
}
