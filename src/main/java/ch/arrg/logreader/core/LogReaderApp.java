package ch.arrg.logreader.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.interfaces.AppCallback;
import ch.arrg.logreader.interfaces.HasBridges;
import ch.arrg.logreader.reader.AbstractReader;
import ch.arrg.logreader.reader.StreamReader;
import ch.arrg.logreader.reader.TailReader;
import ch.arrg.logreader.ui.Window;

// TODO CONF 2 log file

public class LogReaderApp implements AppCallback {
	private final static Logger logger = LoggerFactory.getLogger(LogReaderApp.class);

	private final HasBridges bridgeHandler;
	private HashMap<String, Bridge> bridges = new HashMap<>();

	public LogReaderApp() {
		this.bridgeHandler = new Window(this);
	}

	public void setVisible() {
		bridgeHandler.start();
	}

	/** {@inheritDoc} */
	@Override
	public void openFile(String name, String fileName) throws IOException {
		if (Config.isNativeTail()) {
			TailReader reader = new TailReader(fileName);
			bridgeReader(name, reader);
		} else {
			FileInputStream is = new FileInputStream(fileName);
			openStream(name, is);
		}
	}

	@Override
	public void openStream(String name, InputStream is) throws IOException {
		StreamReader reader = new StreamReader(is);
		bridgeReader(name, reader);
	}

	private void bridgeReader(String name, AbstractReader reader) {
		logger.info("Bridging reader {} with name {}.", reader, name);
		Bridge bridge = new Bridge(reader);
		bridgeHandler.addBridge(name, bridge);
		bridge.start();
		bridges.put(name, bridge);
	}

	@Override
	public void quit() {
		for (Entry<String, Bridge> e : bridges.entrySet()) {
			try {
				e.getValue().stop();
			} catch (IOException ex) {
				logger.warn("Couldn't stop reader with name {} while quitting.", e.getKey(), ex);
			}
		}
		logger.info("Exiting");
		System.exit(0);
	}

	@Override
	public void removeConsumer(String name) {
		logger.info("Stopping bridge {}", name);
		Bridge bridge = bridges.get(name);
		try {
			bridgeHandler.removeBridge(name, bridge);
			bridge.stop();
		} catch (IOException e) {
			logger.warn("Couldn't stop bridge {} with name {}", bridge, name, e);
		}
	}
}
