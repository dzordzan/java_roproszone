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
import java.util.Random;
import java.util.Vector;

public class KK extends Thread {
	static Vector<KK> czaty = new Vector<KK>();
	static Integer ilosc = 0;
	// pierwszy ruch gracz z znakiem O
	static String ruch = "O";
	
	private Socket socket;
	private BufferedReader wejscie;
	private PrintWriter wyjscie;
	
	static String[] plansza = {"_","_","_",
								"_","_","_",
								"_","_","_"};
	private String znak;
	public KK(Socket socket, String znak) {
		this.socket = socket;
		this.znak = znak;
	}

	private void wyslijDoWszystkich(String tekst) {
		for (KK czat : czaty) {
			synchronized (czaty) {
				czat.wyjscie.println(tekst);
			}
		}
	}
	// Wyswietla aktualna grafike gry WSZYSTKIM
	private void info() {
		wyjscie.println("_______________");
		int i = 0;
		for (KK czat : czaty) {
			synchronized (czaty) {
				for (String X : plansza){
					if (i == 0)
						czat.wyjscie.print("|"+X); 
					else if (i == 1)
						czat.wyjscie.print("|"+X);	
					else{
						czat.wyjscie.print("|"+X+"|");
					}
					if (i == 2){
						czat.wyjscie.println();
						i = -1;
					}
					
					i++;
				}
				
			}
		}
		wyjscie.println("_______________");
	}

	public void run() {
		
		// ZWIEKSZ ILSOC AKTUALNYCH GRACZY
		KK.ilosc++;
		String linia;
		
		synchronized (czaty) {
			czaty.add(this);
		}
		try {
			wejscie = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			wyjscie = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream()), true);
			wyjscie.println("Połączony z serwerem. Komenda /end kończy grę.");
			wyjscie.println("Jestes "+this.ilosc+" graczem. Twoj znak to: "+this.znak);
			
			
			wyjscie.println("Podaj cyfrę z przedzialu 1-9 aby wybrac odpowiednie pole:");
			wyjscie.println("_________");
			wyjscie.println("|1|2|3|");
			wyjscie.println("|4|5|6|");
			wyjscie.println("|7|8|9|");
			wyjscie.println("_________");
			//info();
			while (!(linia = wejscie.readLine()).equalsIgnoreCase("/end")) {
				// Sprawdz czy ilosc graczy sie zgada 
				if (this.ilosc != 2){
					this.wyjscie.println("Czekaj na gracza");
					continue;
				}
				// Sprawdz czyj ruch teraz
				if (KK.ruch != this.znak){
					this.wyjscie.println("Aktualnie ruch ma: "+KK.ruch);
					this.wyjscie.println("Czekaj az wyswietli sie nowy rozrys planszy po jego ruchu");
					continue;
				}
				// Pobierz numer pola
				int pole = Integer.parseInt(linia)-1;
				
				// sprawdź czy pole nei jest juz zajete
				if (plansza[pole] != "_"){
					this.wyjscie.println("Te pole jest juz zajety podaj nastepne:");
					continue;
				}
				// Przypisz znak do pola
				plansza[pole] = this.znak;
				
				// Sprawdzamy czy ktos juz nie wygral
				
				// 8 komibnacji
				String [] znaki = {"O", "X"};
				for (String sprZnak : znaki)
				if ( // Poziomo 
					((plansza[0] == sprZnak) && (plansza[1] == sprZnak) && (plansza[2] == sprZnak)) ||
					 ((plansza[3] == sprZnak) && (plansza[4] == sprZnak) && (plansza[5] == sprZnak)) ||
					 ((plansza[7] == sprZnak) && (plansza[7] == sprZnak) && (plansza[8] == sprZnak)) ||
					 // Pionowo
					 ((plansza[0] == sprZnak) && (plansza[3] == sprZnak) && (plansza[6] == sprZnak)) ||
					 ((plansza[1] == sprZnak) && (plansza[4] == sprZnak) && (plansza[7] == sprZnak)) ||
					 ((plansza[2] == sprZnak) && (plansza[5] == sprZnak) && (plansza[8] == sprZnak)) ||
					// Skos
					 ((plansza[0] == sprZnak) && (plansza[4] == sprZnak) && (plansza[8] == sprZnak)) ||
					 ((plansza[2] == sprZnak) && (plansza[4] == sprZnak) && (plansza[6] == sprZnak)) 
						){
					info();
					// Wygrana
					wyslijDoWszystkich("Wygrał gracz ze znakiem "+sprZnak +". Plansza zresetowana");
					// resetuj plansze
					String [] reset = {"_","_","_","_","_","_","_","_","_"};
					KK.plansza = reset;
					
					
					
				}
					
				// Sprawdz czy cala plansza nie jest juz zajeta
				for (int i=0; i<=8; i++){
					if ( KK.plansza[i] != "_"){
						if (i == 8){
							info();
							// Wygrana
							wyslijDoWszystkich("Nikt nie wygral. Plansza zresetowana");
							// resetuj plansze
							String [] reset = {"_","_","_","_","_","_","_","_","_"};
							KK.plansza = reset;
						}
					} else
						break;
					
				}
				// ustaw ruch dla drugiego gracza
				KK.ruch = (KK.ruch=="O")?"X":"O";
				
				// Wyswietl wszystkim aktualne punkty
				info();
				this.wyjscie.println("Podaj nastepna cyfre");
			}
			KK.ilosc--;
			wyslijDoWszystkich("Opuścił grę gracz ze znakiem"+ this.znak);

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