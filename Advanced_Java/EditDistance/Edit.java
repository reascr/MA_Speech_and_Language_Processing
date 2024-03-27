package blatt5;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;

public class Edit {

	//Funktion zum Einlesen eines Files
		public static ArrayList<String> read(String fileName) {

			ArrayList<String> data = new ArrayList<>();

			try {		
				BufferedReader reader = new BufferedReader(new FileReader(fileName));

				String line = "";

				while (line != null) {

					line = reader.readLine();

					if (line != null) {
						data.add(line);
					}

				}
				reader.close();

			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return data;
		}

		//Funktion zum Schreiben von Personendaten in einen File
		public static void write(String fileName, ArrayList<?> data) {

			try {		
				BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

				for (int i = 0; i < data.size(); i++)
					writer.write(data.get(i).toString() +"\n");

				writer.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		//Berechnung der Editierdistanz mittels eines dynamischen Programms (rekursiv)
		public static int computeEDT(String s1, String s2, int c1, int c2, int c3) {

			int n = s1.length();
			int m = s2.length();
			
			// Tabelle für das Speichern von Zwischenwerten (dyn. Programmierung)
			int[][] table = new int[n+1][m+1];


			return computeEDTHelper(s1, s2, c1, c2, c3, n, m, table);
		}
		
		public static int computeEDTHelper(String s1, String s2, int c1, int c2, int c3, int n, int m, int[][] table) {
			
			// base cases (übernommen von Rekursionsformel der Vorlesung)
			
			if (n == 0)
		        return m * c2; // c2=Einfügen
		    if (m == 0)
		        return n * c1; // c1=Löschen
		    
		    // Prüfe ob Ergebnis schon in Tabelle steht
		    if (table[n][m] != 0) {
	            return table[n][m];
	        }
		    
		    // wichtig für die Ersetzenrekursion
		    int cost;
		    // wenn die Zeichen gleich sind, sind keine Änderungen notwendig
		    if (s1.charAt(n - 1) == s2.charAt(m - 1))
		        cost = 0;
		    else
		        cost = c3; // Kosten für Ersetzung
		    
		 // Rekursive Aufrufe für Ersetzung, Löschung und Einfügung
		    int deletion = computeEDTHelper(s1, s2, c1, c2, c3, n - 1, m, table) + c1;
		    int insertion = computeEDTHelper(s1, s2, c1, c2, c3, n, m - 1, table) + c2;
		    int substitution = computeEDTHelper(s1, s2, c1, c2, c3, n - 1, m - 1, table) + cost;

		    // Berechne die kleinsten Kosten 
		    int minCost = Math.min(deletion, Math.min(insertion, substitution));
		    
		    // Speichere Ergebnis in Tabelle
		    table[n][m] = minCost;

		    return minCost;
		}

		//Paarklasse, die zum Sortieren der Treffer nach Distanz verwendet wird
		private static class Pair {

			public int score;
			public String name;

			Pair(int score, String name) {
				this.score = score;
				this.name = name;
			}

			public String toString() {
				//String s = "" + score;
				//return s + " " + name;
				return name;
			}
		}

		
		

		public static void main(String args[]) {

			if (args.length < 7)
	            return;

	        int c1 = Integer.parseInt(args[0]);  // Kosten für Löschen
	        int c2 = Integer.parseInt(args[1]);  // Kosten für Einsetzen
	        int c3 = Integer.parseInt(args[2]);  // Kosten für Ersetzen

	   
	        int B = Integer.parseInt(args[3]);

	        // Einlesen der Eingabedateien mit den Tieren und den Anfragen
	        ArrayList<String> tiere = read(args[4]);
	        ArrayList<String> anfragen = read(args[5]);

	        // Speichert Treffer, die ED Kosten kleiner gleich B haben
	        ArrayList<String> treffer = new ArrayList<>();

	        for (int i = 0; i < anfragen.size(); i++) {
	            ArrayList<Pair> hits = new ArrayList<>();

	            for (int j = 0; j < tiere.size(); j++) {
	            	// berechne Editierkosten für jedes Tier
	                int ed = computeEDT(anfragen.get(i), tiere.get(j), c1, c2, c3);
	                if (ed <= B) {
	                    hits.add(new Pair(ed, tiere.get(j)));
	                }
	            }

	            // Sortiere Treffer hingehend ihrer ED Kosten (aufsteigend)
				hits.sort((p1,p2)->Integer.compare(p1.score,p2.score));

				//Fasse alle Treffer in einen String zusammen
				String hitssortiert = "";
				for (int j = 0; j < hits.size(); j++) {
					hitssortiert += hits.get(j).toString();
		
					if (j != hits.size()-1)
						hitssortiert += ", ";
				}

				treffer.add(hitssortiert);		
			}


	        write(args[6], treffer);


		}

	}
