package Logger;

import java.io.IOException;
import java.util.logging.*;

public class MyLogger {
	private final Logger logger = Logger.getLogger(MyLogger.class.getName());

	public MyLogger() throws IOException {
		Handler handler = new FileHandler("text.txt", true);
		handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
	}

	public Logger getLogger() {
		return logger;
	}
}