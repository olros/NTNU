package Database;

import Components.FileLogger;
import Database.HibernateClasses.User;

import javax.persistence.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Class that is used to connect to the database
 */
public class Hibernate {

	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager em;

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private Hibernate() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets entity manager. Creates a new one if the connection is not open anymore or the entity manager is null
	 *
	 * @return the entity manager
	 */
	public static EntityManager getEm() {
		if (em == null || !em.isOpen()) {
			em = getEntityManagerFactory().createEntityManager();
		}
		return em;
	}

	/**
	 * Gets entity manager factory. Creates a new one if the connection is not open anymore or the entity manager factory is null
	 *
	 * @return the entity manager factory
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
			entityManagerFactory = Persistence.createEntityManagerFactory("Database", getProperties());
		}
		return entityManagerFactory;
	}

	/**
	 * Sets up the password, username and url to the database
	 *
	 * @return a map with the password, username and url
	 */
	private static Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<>();
		try (InputStream input = new FileInputStream("config.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			result.put("hibernate.connection.username", prop.getProperty("username"));
			result.put("hibernate.connection.password", prop.getProperty("password"));
			result.put("hibernate.connection.url", prop.getProperty("database_url"));
		} catch (IOException ex) {
			FileLogger.getLogger().log(Level.FINE, ex.getMessage());
			FileLogger.closeHandler();
		}
		return result;
	}

	/**
	 * Register a new user in the database
	 *
	 * @param username the username
	 * @param hash     the hash
	 * @param salt     the salt
	 * @return if the registration was successful
	 */
	public static boolean registerUser(String username, String hash, String salt) {
		if (username == null || hash == null || salt == null) {
			throw new IllegalArgumentException();
		}
		EntityTransaction et = null;
		boolean isSuccess = false;
		try {
			et = getEm().getTransaction();
			et.begin();
			User user = new User();
			user.setUsername(username);
			user.setHash(hash);
			user.setSalt(salt);
			getEm().persist(user);
			et.commit();
			isSuccess = true;
		} catch (Exception e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
		return isSuccess;
	}

	/**
	 * Update user.
	 *
	 * @param user the user to update.
	 */
	public static void updateUser(User user) {
		EntityTransaction et = null;
		try {
			et = getEm().getTransaction();
			et.begin();
			getEm().merge(user);
			getEm().flush();
			et.commit();
		} catch (Exception e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
	}

	/**
	 * Gets salt of user.
	 *
	 * @param username username of user.
	 * @return the salt.
	 * @throws NoResultException if the user was not found.
	 */
	public static String getSalt(String username) throws NoResultException {
		EntityTransaction et = null;
		try {
			et = getEm().getTransaction();
			et.begin();
			User user = em.createQuery(
					"select e from User e where e.username =:username",
					User.class)
					.setParameter("username", username)
					.getSingleResult();
			et.commit();
			return user.getSalt();
		} catch (NoResultException e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		} catch (Error | Exception e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		}
	}

	/**
	 * Gets user.
	 *
	 * @param username username of user.
	 * @return the user.
	 */
	public static User getUser(String username) {
		EntityTransaction et = null;
		try {
			et = getEm().getTransaction();
			et.begin();
			User user = getEm().createQuery(
					"select e from User e where e.username =:username",
					User.class)
					.setParameter("username", username)
					.getSingleResult();
			et.commit();
			return user;
		} catch (Exception e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			throw e;
		}
	}

	/**
	 * Login user
	 *
	 * @param username the username.
	 * @param hash     the hash.
	 * @return if login was successful.
	 */
	public static boolean login(String username, String hash) {
		EntityTransaction et = null;
		try {
			et = getEm().getTransaction();
			et.begin();
			User user = getEm().createQuery(
					"select e from User e where e.username =:username and e.hash =:hash",
					User.class)
					.setParameter("username", username)
					.setParameter("hash", hash)
					.getSingleResult();
			et.commit();
			return (user != null);
		} catch (Exception e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
			return false;
		}
	}

	/**
	 * Delete user.
	 *
	 * @param username the username.
	 */
	public static void deleteUser(String username) {
		EntityTransaction et = null;
		try {
			User user = getUser(username);
			et = getEm().getTransaction();
			et.begin();
			getEm().remove(user);
			et.commit();
		} catch (Exception e) {
			if (et != null) {
				et.rollback();
			}
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
	}

	/**
	 * Method that sets up the database for the application.
	 */
	public static void setupDatabase() {
		entityManagerFactory = Persistence.createEntityManagerFactory("Database-setup", getProperties());
		entityManagerFactory.close();
	}
}
