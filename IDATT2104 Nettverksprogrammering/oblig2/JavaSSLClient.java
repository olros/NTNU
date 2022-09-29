import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

/**
 * @web http://java-buddy.blogspot.com/
 */
public class JavaSSLClient {

	static final int port = 8000;

	public static void main(String[] args) {

		SSLSocketFactory sslSocketFactory =
				(SSLSocketFactory)SSLSocketFactory.getDefault();
		try {
			Socket socket = sslSocketFactory.createSocket("localhost", port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			try (BufferedReader bufferedReader =
					     new BufferedReader(
							     new InputStreamReader(socket.getInputStream()))) {
				Scanner scanner = new Scanner(System.in);
				while(true){
					System.out.println("Enter something:");
					String inputLine = scanner.nextLine();
					if(inputLine.equals("q")){
						break;
					}

					out.println(inputLine);
					System.out.println(bufferedReader.readLine());
				}
			}

		} catch (IOException ex) {
			Logger.getLogger(JavaSSLClient.class.getName())
					.log(Level.SEVERE, null, ex);
		}

	}

}