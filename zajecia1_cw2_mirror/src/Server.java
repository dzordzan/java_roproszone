import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static ServerSocket server;
	private static final int PORT = 2345;

	public static void main(String args[]) {
		String linia;
		try {
			server = new ServerSocket(PORT);
			System.out.println("Serwer uruchomiony...");
			while (true) {
				Socket socket = server.accept();
				Scanner in = new Scanner(socket.getInputStream());
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println("Prosty serwer ECHO, komenda /end kończy działanie.");
				boolean koniec = false;
				while (!koniec) {
					linia = in.nextLine();
					if (linia.trim().toLowerCase().equals("/end")) {
						koniec = true;
					} else {
						String reverseWord = "";
						// wiem że jest gotowa funkcja, ale chuj zrobimy ręcznie
						// nauczymy was jak działają kurwa pętle
						for (int i = linia.length()-1; i >= 0; i--) 
						{
							reverseWord += linia.charAt(i);
						}
						
						out.println(reverseWord);
						
						
					}
				}
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
