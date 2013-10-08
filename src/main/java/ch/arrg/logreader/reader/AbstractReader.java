package ch.arrg.logreader.reader;

import java.io.IOException;

import ch.arrg.logreader.interfaces.Consumer;

// TODO CONF 3 Readers read interval

public interface AbstractReader {

	public abstract void start();

	public abstract void stop() throws IOException;

	/** Called before start. */
	public abstract void setConsumer(Consumer cons);

}