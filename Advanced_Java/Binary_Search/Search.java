package blatt3;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;


public class Search {

	//Funktion zum Einlesen eines Files (übernommen von Beispielcode)
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

	//Funktion zum Schreiben in einen File (übernommen von Beispielcode)
	public static void write(String fileName, ArrayList<?> data) {

		try {		
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			for (int i = 0; i < data.size(); i++)
				writer.write(data.get(i)+"\n");

			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//Filmobjekt (modifiziert übernommen von Beispielcode)
	private static class Movie {

		private String title;
		private String description;

		public Movie(String info) {
			String[] vpr = info.split("\t");
			if (vpr.length == 2) {
				title = vpr[0];
				description = vpr[1];
			}
		}

		public String getTitle() {
			return title;
		}
		
		public String getDescription() {
			return description;
		}

	};

	//Film Comparator (übernommen von Beispielcode)
	private static class MovieByTitleComparator implements Comparator<Movie> {
		public int compare(Movie m1, Movie m2) {
			return m1.getTitle().compareTo(m2.getTitle());
		}
	}
	
	// Klasse für Knoten
	private static class Node {
		private Movie movie;
		private String key;
		private Node left;
		private Node right; 
		private Node parent; //nicht sicher, ob parent unbedingt benötigt wird
		private Comparator<Movie> comparator;
		
		//Konstruktor
		
		public Node(Movie movie) {
			this.movie = movie;
			this.key = movie.getTitle();
			this.left = null;
			this.right = null;
			this.parent = null;
			this.comparator = new MovieByTitleComparator();
		}
	}
	
	// Klasse für binären Suchbaum
	
	private static class BinarySearchTree {
		private Node root;
		
		// Konstruktor
		public BinarySearchTree() {
			this.root = null;
		}
		
		
		// Methode zum Einfügen von Filmen, Orientierung am Pseudocode der VL

		public void insert(Movie movie) {
			insertNode(root, movie);
			}
				
		private void insertNode(Node v, Movie x) {
            
            if (v == null) {
            	root = new Node(x);
            	return;
            }
            
            if (v.key.equals(x.getTitle())) {
            	return;
            }
            if(v.comparator.compare(x, v.movie) < 0 ) {
                if (v.left != null) {
                    insertNode(v.left, x);
                } else {
                    Node newNode = new Node(x);
                    newNode.parent = v;
                    v.left = newNode;
                }
            } else {
                if (v.right != null) {
                    insertNode(v.right, x);
                } else {
                    Node newNode = new Node(x);
                    newNode.parent = v;
                    v.right = newNode;
                }
            }
        }
		
		// Methode zum Finden von Filmen anhand Schlüssel (=Titel), Orientierung am Pseudocode der VL
		public Movie find(String title) {
	        return findNode(root, title);
	    }
		
		private Movie findNode(Node v, String x) {
			if (v == null) {
		        return null;
		    }
		    
		    if (v.key.equals(x)) {
		        return v.movie;
		    }
		    
		    /**
		     * Im Folgenden habe ich den String Comparator verwendet, um den Schlüssel des aktuellen Knotens (=Titel)
		     * und den gesuchten Titel zu vergleichen. Mir war nicht ganz klar, wie ich die Methode so umschreiben
		     * kann, dass ich den MovieByTitleComparator verwenden kann, da ich von dem Titel als String nicht direkt auf
		     * das Movie-Objekt zugreifen kann. Man könnte vielleicht ein neues Movie-Objekt mit dem Titel und 
		     * einer leeren Beschreibung erstellen, etwa
		     * 
		     * String info = x + "\t" + " ";
		     * new Movie(x)
		     * 
		     * So könnte man den Comparator verwenden.
		     */
		    
		    if (x.compareTo(v.key) < 0) { 
		        if (v.left != null) {
		            return findNode(v.left, x);
		        }
		        
		    } else {
		        if (v.right != null) {
		            return findNode(v.right, x);
		        }
		    }
		    
		    return null;
		}

		
	}
	
	
	

	public static void main(String args[]) {


		if (args.length < 3)
			return;

		//Eingabedatei Filme
		String movieFile = args[0];

		//Datenstruktur zum Verwalten der eingelesenen Elemente
		ArrayList<String> sdata = read(movieFile);

		//Datenstruktur zur Verwaltung der Filmobjekte
		ArrayList<Movie> movies = new ArrayList<Movie>();

		//Konvertierung der eingelesenen Zeilen zu Filmobjekten im Konstruktor
		for (int i = 0; i < sdata.size(); i++)
			movies.add(new Movie(sdata.get(i)));
		
		//Anlegen eines Binären Suchbaums
		BinarySearchTree tree = new BinarySearchTree();
		
		// Einfügen der Filme der Eingabedatei 1 in den Baum
		for (Movie movie: movies) {
			tree.insert(movie);
		}
		
		//Eingabedatei Filmtitel
		String titleList = args[1];
		ArrayList<String> titles = read(titleList);

		//Anlegen einer Datenstruktur zur Verwaltung der Ausgabe
		ArrayList<String> descriptions = new ArrayList<>();
		
		for(String title: titles) {
			Movie movie = tree.find(title);
			String moviedescription = movie.getTitle() + "\t" + movie.getDescription();
			descriptions.add(moviedescription);
			
		}

		//Schreiben in Ausgabefile
		write(args[2], descriptions);

	}

}
