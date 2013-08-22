package ch.arrg.logreader.core;

import java.io.InputStream;

import ch.arrg.logreader.LogReader;

public class Resources {
	public static InputStream getWindowIcon() {
		return LogReader.class.getResourceAsStream("icon.png");
	}

	public static InputStream getConfFile() {
		return LogReader.class.getResourceAsStream("base-conf.txt");
	}
}
