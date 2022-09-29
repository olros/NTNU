package Main;

import Components.FileLogger;
import Database.Hibernate;

import java.util.logging.Level;

/**
 * Setup database class
 */
public class SetupDatabase {

	public static void main(String[] args) {
		setupDatabase();
	}

	/**
	 * Sets up a working database for the user
	 */
	private static void setupDatabase() {
		try {
			Hibernate.setupDatabase();
			System.out.println("Database setup complete. You can now run the program");
			Hibernate.getEm().close();
			Hibernate.getEntityManagerFactory().close();
		} catch (Exception e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
	}
}
