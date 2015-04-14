import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static ServerSocket server;
	private static final int PORT = 2345;

	private static String getQuote() {
		String adres = "polishwords.com.pl";
		String sciezka = "/grono/api.php?method=randomQuote";

		String content = "";
		//System.out.println("Pobieram dane ze strony: " + adres);
		try {
			// Ustanowienie połączenia z serwerem (port 80)
			Socket socket = new Socket(adres, 80);
			// Utworzenie strumienia danych wejściowych i wyjściowych
			PrintStream out = new PrintStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// wysłanie zapytania (zgodnie z protokołem HTTP)
			out.print("GET " + sciezka + " HTTP/1.1" + "\r\n");
			out.print("Host: " + adres + "\r\n");
			out.print("Connection: keep-alive\r\n");
			out.print("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.86\r\n");
			out.print("Accept-Encoding: gzip, deflate, sdch\r\n");
			out.print("Accept-Language: en-US,en;q=0.8,pl;q=0.6\r\n");
			out.print("Cache-Control: no-cache\r\n");
			out.print("User-Agent: Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36\r\n");

			
			out.print("\r\n");
			out.flush();
			// odczyt danych z serwera
			String linia = in.readLine();
			while (linia != null) {
				content = content + linia;
				linia = in.readLine();
			}
			// Zamknięcie strumieni
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("Błąd");
		}
		return content;
	}

	public static void main(String args[]) {
		String linia;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Serwer uruchomiony...");
			while (true) {
				Socket socket = server.accept();
				Scanner in = new Scanner(socket.getInputStream());
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

				String quote = Server.getQuote();
				
				out.println(quote);
				
				in.close();
				socket.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
