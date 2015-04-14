/* Andrzej Piszczek (c) 2015 
 * andpis58@gmail.com
 * */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.Vector;

public class CzatObsluga extends Thread {
	static Vector<CzatObsluga> czaty = new Vector<CzatObsluga>();
	private Socket socket;
	private BufferedReader wejscie;
	private PrintWriter wyjscie;
	private String nick;

	private String[] censouredWords = {"kurwa", "chuj", "wilux", "voldemort"};
	
	private String replaceCensouredWords(String text)
	{
		for (String censouredWord : censouredWords){
			text = text.replaceAll(censouredWord, "******");
		}
		return text;
	}
	private Boolean checkIfNickExist()
	{
		for (CzatObsluga czat : czaty) {
			synchronized (czaty) {
				if (czat != this )
					if ( czat.nick.equalsIgnoreCase(this.nick) ){
						wyjscie.println("Taki nick istnieje!");
						return true;
				}
			}
				
		}
		return false;
	}
	
	private void wyslijDo(String nick, String tekst){
		for (CzatObsluga czat : czaty) {
			synchronized (czaty) {
				if (czat != this && czat.nick.equalsIgnoreCase(nick))
					czat.wyjscie.println("<" + nick + "> " + tekst);
			}
		}
	}
	public CzatObsluga(Socket socket) {
		this.socket = socket;
	}
	public static void serverMessage(String tekst){
		for (CzatObsluga czat : czaty) {
			synchronized (czaty) {
				czat.wyjscie.println("<SERVER> " + tekst);
			}
		}
	}
	private void wyslijDoWszystkich(String tekst) {
		for (CzatObsluga czat : czaty) {
			synchronized (czaty) {
				if (czat != this)
					czat.wyjscie.println("<" + nick + "> " + tekst);
			}
		}
	}

	private void info() {
		wyjscie.print("Witaj " + nick + ", aktualnie czatują: ");
		for (CzatObsluga czat : czaty) {
			synchronized (czaty) {
				if (czat != this)
					wyjscie.print(czat.nick + " ");
			}
		}
		wyjscie.println();
	}

	public void run() {
		String linia;
		synchronized (czaty) {
			czaty.add(this);
		}
		try {
			wejscie = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			wyjscie = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
			wyjscie.println("Połączony z serwerem. Komenda /end kończy połączenie.");
			
			wyjscie.println("/private @nick {wysyła wiadomosc prywatna}");
			wyjscie.println("/list {lista aktualnie zalogowanych uzytkownikow}");
			
			Boolean emptyNick = true;
			while (emptyNick){
				wyjscie.println("Podaj swój nick: ");
				nick = wejscie.readLine();
				emptyNick = checkIfNickExist();
			}
			System.out.println("Do czatu dołączył: " + nick);
			wyslijDoWszystkich("Pojawił się na czacie");
			info();
			while (!(linia = wejscie.readLine()).equalsIgnoreCase("/end")) {
				
				linia = replaceCensouredWords(linia);
				String command;
				String param1 = "";
				//w przypadku polecen bez parametrow
				if (linia.indexOf(" ") == -1)
				{
				command = linia; 
				} else {
				command = linia.substring(0, linia.indexOf(" "));
				param1 = linia.substring(linia.indexOf(" @")+2, linia.indexOf(" ", linia.indexOf(" @")+1));
				
				}
				String rest = linia.substring(command.length(), linia.length());
				switch (command){
				case "/private": 
					wyslijDo(param1, rest);
					break;
				case "/list": 
					info();
					break;
				default:
					wyslijDoWszystkich(linia);
				}
				
			
			
			}
			wyslijDoWszystkich("Opuścił czat");
			System.out.println("Czat opuścił: " + nick);
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			try {
				wejscie.close();
				wyjscie.close();
				socket.close();
			} catch (IOException e) {
			} finally {
				synchronized (czaty) {
					czaty.removeElement(this);
				}
			}
		}
	}
}