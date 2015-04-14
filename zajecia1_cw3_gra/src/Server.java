import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static ServerSocket server;
	private static final int PORT = 2345;

	public static void main(String args[]) {
		String linia;
		/* GENERUJEMY LOSOWĄ STAŁĄ LICZBĘ */
		Random generator = new Random(); 
		int randomNumber = generator.nextInt(100);
		
		
		try {
			server = new ServerSocket(PORT);
			System.out.println("Serwer uruchomiony...");
			while (true) {
				Socket socket = server.accept();
				Scanner in = new Scanner(socket.getInputStream());
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				out.println("Prosta gra. Podaj liczbe lub wpisz /end -- kończy działanie.");
				
				boolean koniec = false;
				while (!koniec) {
					linia = in.nextLine();
					if (linia.trim().toLowerCase().equals("/end")) {
						koniec = true;
					} else {
						int userNumber = Integer.parseInt(linia);
						if (userNumber < randomNumber){
							out.println("Za mała!");
						} else if (userNumber > randomNumber) {
							out.println("Za duża!");
						} else {
							out.println("Bingo!");
						}
						
				
						
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
