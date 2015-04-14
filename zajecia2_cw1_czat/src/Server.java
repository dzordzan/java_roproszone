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
			new ServerCommand().start();;
			
			server = new ServerSocket(PORT);
			System.out.println("Czat Serwer uruchomiony na porcie: " + PORT);
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
class ServerCommand extends Thread {
	Scanner keyboard = new Scanner(System.in);
	public void run() {
		String tekst = "";
			while (true ){
				tekst = keyboard.nextLine();
				CzatObsluga.serverMessage(tekst);
			}
		}
}
