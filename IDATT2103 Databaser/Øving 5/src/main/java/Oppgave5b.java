import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Oppgave5b {

	private static Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<>();
		try (InputStream input = new FileInputStream("config.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			result.put("username", prop.getProperty("username"));
			result.put("password", prop.getProperty("password"));
			result.put("database_url", prop.getProperty("database_url"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static Connection connect() throws SQLException {
		Map<String, String> properties = getProperties();
		String user = properties.get("username");
		String pass = properties.get("password");
		String url = properties.get("database_url");
		return DriverManager.getConnection(url, user, pass);
	}

	public static void getInfoByIsbn(String isbn) {
		try {
			Connection connection = connect();
			PreparedStatement stmt;
			stmt = connection.prepareStatement("select forfatter, tittel, count(*) as antall " +
					"from boktittel join eksemplar on boktittel.isbn = eksemplar.isbn and eksemplar.isbn = ?");
			stmt.setString(1, isbn);
			printInfo(stmt.executeQuery());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void printInfo(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			System.out.println("-----------------------\n" +
					"Forfatter: " + resultSet.getString("forfatter") + "\n" +
					"Tittel: " + resultSet.getString("tittel") + "\n" +
					"Antall: " + resultSet.getInt("antall"));
		}
	}

	public static int updateInfoByIsbn(String isbn, String rentedBy, int copyNr) {
		try {
			Connection connection = connect();
			PreparedStatement statement;
			statement = connection.prepareStatement("update eksemplar set laant_av = ? " +
					"where isbn = ? and eks_nr = ? and laant_av is null;");
			statement.setString(1, rentedBy);
			statement.setString(2, isbn);
			statement.setInt(3, copyNr);
			return statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static void main(String[] args) {
		getInfoByIsbn("0-07-241163-5");
		System.out.println("Rows affected: " + updateInfoByIsbn("0-07-241163-5", "Per Olsen", 1));
	}
}
