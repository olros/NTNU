package Components;

import Database.Hibernate;
import Main.ApplicationManager;
import Roots.LoginRoot;

import javax.persistence.PersistenceException;
import java.util.logging.Level;

/**
 * Authentication class for authenticating username and password for each user
 */
public final class Authentication {

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private Authentication() {
		throw new IllegalStateException("Can not make instance of utility class");
	}

	/**
	 * Register a new user
	 * It will encrypt the password before inserting into database
	 *
	 * @param username the username
	 * @param password the password
	 * @return if method was successful
	 */
	public static boolean register(String username, String password) {
		try {
			// Encrypt password
			String encrypter = Encrypter.encrypt(password, null);
			// Get salt and hash
			String hash = Encrypter.getHash(encrypter);
			String salt = Encrypter.getSalt(encrypter);

			return Hibernate.registerUser(username, hash, salt);
		} catch (ExceptionInInitializerError | NoClassDefFoundError e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		} catch (IllegalArgumentException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			return false;
		}
	}

	/**
	 * LogIn method is used when the user is trying to log in
	 * will compare password from db to the entered password
	 *
	 * @param username of user
	 * @param password of user
	 * @return if login was successful
	 */
	public static boolean logIn(String username, String password) {
		try {
			// Getting salt from db using username
			String salt = Hibernate.getSalt(username);
			// Generating hash using salt
			String encrypter = Encrypter.encrypt(password, salt);
			String hash = Encrypter.getHash(encrypter);

			// Try to login
			if (Hibernate.login(username, hash)) {
				UserInfo.initializeUser(Hibernate.getUser(username));
				return true;
			} else {
				return false;
			}
		} catch (ExceptionInInitializerError | NoClassDefFoundError e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		} catch (PersistenceException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			return false;
		}
	}

	/**
	 * Checks if a user with a username and password is the current user logged in
	 *
	 * @param username of user
	 * @param password of user
	 * @return if this user is the current user
	 */
	public static boolean isCurrentUser(String username, String password) {
		try {
			// Getting salt from db using username
			String salt = Hibernate.getSalt(username);
			// Generating hash using salt
			String encrypter = Encrypter.encrypt(password, salt);
			String hash = Encrypter.getHash(encrypter);

			// Checks if the current user's hash is equal to the hash from the password entered
			return UserInfo.getUser().getHash().equals(hash);
		} catch (ExceptionInInitializerError | NoClassDefFoundError | PersistenceException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		}
	}

	/**
	 * Deletes the current logged in user
	 * will compare password from db to the entered password
	 *
	 * @param username     of user
	 * @param password     of user
	 * @param confirmation if the user confirmed to delete the user
	 * @return if deleting the user was successful
	 */
	public static boolean deleteUser(String username, String password, boolean confirmation) {
		try {
			// Checks if this is the current user and the user has given consent
			if (isCurrentUser(username, password) && confirmation) {
				Hibernate.deleteUser(username);
				return true;
			} else {
				return false;
			}
		} catch (ExceptionInInitializerError | NoClassDefFoundError | PersistenceException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		}
	}

	/**
	 * Log out the user and redirect to login scene
	 *
	 * @return loginRoot the root the user is sent to
	 */
	public static LoginRoot logout() {
		UserInfo.logOut();
		Hibernate.getEm().clear();
		LoginRoot loginRoot = new LoginRoot();
		ApplicationManager.setRoot(loginRoot);
		// Returning loginRoot allows adding a message when logging out the user
		return loginRoot;
	}
}
