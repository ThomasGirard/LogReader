package ch.arrg.logreader.interfaces;

import ch.arrg.logreader.core.Bridge;

public interface HasBridges {

	void start();

	void addBridge(String name, Bridge bridge);

	void removeBridge(String name, Bridge bridge);
}
