package ch.arrg.logreader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.arrg.logreader.core.Config;
import ch.arrg.logreader.core.LogReaderApp;
import ch.arrg.logreader.core.Resources;

// TODO FEAT highlight occurences
// TODO FEAT collapsible exceptions
// TODO FEAT load and restore session

// TODO BUG 2 with -n, old lines are removed only on refilter()

/**
 * This class is the entry point of LogReader.
 */
public class LogReader {
	private final static Logger logger = LoggerFactory.getLogger(LogReader.class);

	public static void main(String[] args) {
		try {

			// Read options
			Options opts = createOptions();
			CommandLineParser clp = new PosixParser();
			CommandLine cl = clp.parse(opts, args);

			// Load config
			Config.fromCommandLine(cl);
			loadProperties(cl);

			// If help, stop here.
			if (cl.hasOption('h')) {
				printHelp(opts);
				return;
			}

			createAndRun(cl);
		} catch (Exception e) {
			logger.error("Exception in main.", e);
		}
	}

	private static void loadProperties(CommandLine cl) throws IOException {
		// Load base properties (from jar)
		logger.info("Reading default properties.");
		Properties props = new Properties();
		props.load(Resources.getConfFile());

		// Read user properties if they are defined
		if (cl.hasOption("props")) {
			String propPath = cl.getOptionValue("props");
			try (InputStream fis = new FileInputStream(propPath)) {
				logger.info("Reading user properties.");
				Properties userProps = new Properties(props);
				userProps.load(fis);

				props = userProps;
			} catch (IOException e) {
				logger.warn("Could not read user properties.", e);
			}
		}

		Config.fromProperties(props);
	}

	private static void createAndRun(CommandLine cl) throws Exception {
		LogReaderApp lr = new LogReaderApp();
		createReaders(cl, lr);
		lr.setVisible();
	}

	private static void createReaders(CommandLine cl, LogReaderApp lr) {
		if (cl.hasOption("f")) {
			String[] files = cl.getOptionValues('f');
			for (String fname : files) {
				try {
					lr.openFile(fname, fname);
				} catch (IOException e) {
					logger.error("Couldn't create a file reader.", e);
				}
			}
		} else {
			try {
				InputStream is = System.in;
				lr.openStream("stdin", is);
			} catch (IOException e) {
				logger.error("Couldn't create reader for stdin.", e);
			}
		}
	}

	private static void printHelp(Options opts) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("logreader", opts, true);
	}

	@SuppressWarnings("static-access")
	private static Options createOptions() {
		// Parse command line
		Options opts = new Options();
		opts.addOption("h", false, "prints this message.");
		opts.addOption("u", false, "use native tail command (only works with -f).");
		opts.addOption("n", true, "keep at most <arg> lines, throw older ones.");

		Option oProps = OptionBuilder.withLongOpt("props").hasArg()
				.withDescription("a path to a properties file that will override the default properties.").create();
		opts.addOption(oProps);

		Option oNotif = OptionBuilder.withLongOpt("notifications")
				.withDescription("display a notification on screen when some string is read in a file.").create();
		opts.addOption(oNotif);

		Option o = new Option("f", true, "use one or multiple files as input instead of stdin");
		o.setArgs(Option.UNLIMITED_VALUES);
		opts.addOption(o);

		return opts;
	}
}
