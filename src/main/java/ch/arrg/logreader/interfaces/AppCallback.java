package ch.arrg.logreader.interfaces;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for types that can handle UI callbacks. Methods in this interface
 * correspond to actions the user may trigger.
 */
public interface AppCallback {
	public void openStream(String name, InputStream is) throws IOException;

	public void openFile(String prettyName, String filePath) throws IOException;

	void removeConsumer(String name);

	public void quit();
}
