package ch.arrg.logreader.reader;

import java.io.IOException;

import ch.arrg.logreader.interfaces.Consumer;

public interface AbstractReader {

	public abstract void start();

	public abstract void stop() throws IOException;

	public abstract void setConsumer(Consumer cons);

}