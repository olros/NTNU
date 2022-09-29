package Components;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;

/**
 * Encrypter class for encrypting password using hash and salt
 */

public final class Encrypter {

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private Encrypter() {
		throw new IllegalStateException("Can not make instance of utility class");
	}

	/**
	 * Encryption used for the password, method is used for both making and verifying an encrypted password.
	 *
	 * @param password String password that the user har entered
	 * @param saltest  String hex values for the salt
	 * @return String combination of both "hash|salt", uses splitters to get either
	 */

	public static String encrypt(String password, String saltest) {
		if (password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null");
		}
		try {
			// Select the message digest for the hash computation -> SHA-256
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// Generate the random salt
			byte[] salt;
			if (saltest == null) {
				SecureRandom random = new SecureRandom();
				salt = new byte[16];
				random.nextBytes(salt);
			} else {
				salt = buildBytes(saltest);
			}
			// Passing the salt to the digest for the computation
			md.update(salt);
			String salted = buildHexString(salt);
			// Generate the salted hash
			byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
			//Hashed password
			String hashed = buildHexString(hashedPassword);
			return hashed + "|" + salted;
		} catch (NoSuchAlgorithmException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
		return null;
	}

	/**
	 * Byte-to-Hex converter
	 *
	 * @param bytes is an array of byte
	 * @return String bytes in hex
	 */
	private static String buildHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			// Convert from byte to hex
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * Hex-to-byte converter
	 *
	 * @param hex input to get an byte array
	 * @return byte[] converted from hex
	 */
	private static byte[] buildBytes(String hex) {
		byte[] b = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			int v = Integer.parseInt(hex.substring(i, i + 2), 16);
			b[i / 2] = (byte) v;
		}
		return b;
	}

	/**
	 * Used to get the hash string from the encrypt function
	 *
	 * @param s the output from encrypt
	 * @return String hash
	 */
	// Split string from the encryption to Hash using the splitter
	public static String getHash(String s) {
		int index = s.indexOf('|');
		if (index < 0) {
			return null;
		}
		return s.substring(0, index);
	}

	/**
	 * Used to get the salt string from the encrypt function
	 *
	 * @param s the output from encrypt
	 * @return String salt
	 */
	// Split string from the encryption to Salt using the splitter
	public static String getSalt(String s) {
		int index = s.indexOf('|');
		if (index < 0) {
			return null;
		}
		return s.substring(index + 1);
	}
}
