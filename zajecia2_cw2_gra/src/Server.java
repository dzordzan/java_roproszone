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
			System.out.println("Gra Serwer uruchomiony na porcie: " + PORT);
			Random generator = new Random(); 
			CzatObsluga.cyfra = generator.nextInt(100);
			while (true) {
				Socket socket = server.accept();
				InetAddress addr = socket.getInetAddress();
				System.out.println("Połączenie z adresu: " + addr.getHostName()
						+ " [" + addr.getHostAddress() + "]");
				new CzatObsluga(socket).start();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	

}
