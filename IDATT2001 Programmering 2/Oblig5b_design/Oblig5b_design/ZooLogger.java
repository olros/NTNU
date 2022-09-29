package Oblig5b_design;

import java.io.IOException;
import java.util.logging.*;

public class ZooLogger {

	private static Handler handler;
	private static Logger logger;

	// Singleton pattern
	private ZooLogger() {}

	/**
	 * Creates instance of logger and handler
	 *
	 * @return an static instance of logger
	 */
	public static Logger getLogger() {
		try {
			if (logger == null) {
				logger = Logger.getLogger(ZooLogger.class.getName());
				// Creates a fileHandler, that streams all log in to a txt.file
				handler = new FileHandler("log.log", true);
				// SimpleFormatter, changes log format from XML to a human readable format
				handler.setFormatter(new SimpleFormatter());
				// SetLevel, sets a bar for which logs that will be logged/handled
				handler.setLevel(Level.ALL);
				logger.addHandler(handler);
				logger.setLevel(Level.ALL);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return logger;
	}

	public static void closeHandler() {
		handler.close();
	}
}