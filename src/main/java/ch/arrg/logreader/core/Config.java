package ch.arrg.logreader.core;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO IMPR javadoc
// TODO IMPR license

public class Config {
	private final static Logger logger = LoggerFactory.getLogger(Config.class);

	private static CommandLine commandLine;
	private static Properties props;

	public static void fromCommandLine(CommandLine cl) {
		commandLine = cl;
	}

	public static void fromProperties(Properties properties) {
		if (props == null) {
			props = properties;
		} else {
			props.putAll(properties);
		}
	}

	public static boolean isNativeTail() {
		return commandLine.hasOption('u');
	}

	public static int getMaxLines() {
		// TODO BUG 2 bad arguments (especially in -n) will cause problems. Config checking should be fail-on-load not fail-on-use.
		int max = -1;
		if (commandLine.hasOption('n')) {
			String opt = commandLine.getOptionValue('n');
			try {
				max = Integer.parseInt(opt);
			} catch (NumberFormatException e) {
				logger.warn("Bad number [{}] provided for -n option, will be ignored.", opt);
			}
		}

		if (max <= 0) {
			max = -1;
		}

		return max;
	}

	public static boolean notificationsEnabled() {
		return commandLine.hasOption("notifications");
	}

	public static boolean getBoolProp(String key) {
		return Boolean.parseBoolean(getPropTrim(key));
	}

	public static String getStringProp(String key) {
		return getProp(key);
	}

	public static String[] getStringArrayProp(String key) {
		String joined = getProp(key);
		String[] split = joined.split(";");
		return split;
	}

	public static int getIntProp(String key) {
		try {
			return Integer.parseInt(getPropTrim(key));
		} catch (NumberFormatException e) {
			logger.error("Malformed int property for key [{}]", key);
			return -1;
		}
	}

	private static String getPropTrim(String key) {
		return getProp(key).trim();
	}

	private static String getProp(String key) {
		String prop = props.getProperty(key);
		if (prop == null) {
			logger.error("Property not found [{}]", key);
		}

		return prop;
	}
}
