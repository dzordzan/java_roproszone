/* Andrzej Piszczek (c) 2015 
 * andpis58@gmail.com
 * */
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	private static ServerSocket server;
	private static final int PORT = 2345;
	
	
	public static void main(String[] args) {
		try {
			server = new ServerSocket(PORT);
			System.out.println("Gra KÓŁKO KRZYRZYK. Serwer uruchomiony na porcie: " + PORT);

			while (true) {
				Socket socket = server.accept();
				InetAddress addr = socket.getInetAddress();
				System.out.println("Połączenie z adresu: " + addr.getHostName()
						+ " [" + addr.getHostAddress() + "]");
				
				/* Pole statyczne sprawdzajace czy jest juz 2 gracza */
				
				if (KK.ilosc == 0)
					new KK(socket, "O").start();
				else if (KK.ilosc == 1)
					new KK(socket, "X").start(); else{
						 new PrintWriter(new OutputStreamWriter(
									socket.getOutputStream()), true).println("Serwer pełny!");
					}
				
				/* Jeśli jest już 2 graczy lub liczba się zmieni (gdy np. jeden wydzie, to klient zostanie 
				 * "wpuszczony" */
				
				
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	

}
