package Components;

import java.io.IOException;
import java.util.logging.*;

/**
 * Logger class for logging text and exceptions
 */

public final class FileLogger {

	private static Handler handler;
	private static Logger logger;

	/**
	 * Use of Singleton pattern
	 */
	private FileLogger() {
	}

	/**
	 * Creates instance of logger and handler
	 *
	 * @return an static instance of logger
	 */
	public static Logger getLogger() {
		try {
			if (logger == null) {
				logger = Logger.getLogger(FileLogger.class.getName());
				// Creates a fileHandler, that streams all log in to a .log file
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

	/**
	 * Closes handler, used after somethings been logged.
	 * Is necessary for not creating multiple log files.
	 */
	public static void closeHandler() {
		handler.close();
	}
}

