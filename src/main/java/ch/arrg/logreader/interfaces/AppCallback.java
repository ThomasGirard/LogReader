package ch.arrg.logreader.interfaces;

import java.io.IOException;
import java.io.InputStream;

public interface AppCallback {
	public void openStream(String name, InputStream is) throws IOException;

	public void openFile(String name, String fileName) throws IOException;

	void removeConsumer(String name);

	public void quit();
}
