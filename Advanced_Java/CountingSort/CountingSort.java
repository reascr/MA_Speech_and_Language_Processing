package blatt2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;



public class CountingSort {
	static int[] prefixSum; // Array um die Präfixsummen zu speichern, die in CountingSort() erstellt werden
	static Person[] sortedPersons; // Speichern der sortierten Personen für das spätere Schreiben in die Ausgabedatei
	
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
	
	//Funktion zum Schreiben von Personendaten in einen File (mod. übernommen von Beispielcode)
	
			public static void write(String fileName, Person[] data) {

				try {		
					BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

					for (int i = 0; i < data.length; i++)
						if(data[i] instanceof Person) {// check whether it is a Person 
						writer.write(data[i].toString() +"\n");
						}

					writer.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
	//Klasse Person (übernommen von Beispielcode)
			private static class Person {

				private String name;
				private String state;
				private char sex;
				private int age;
				private int ID;
			
				//Constructor
				public Person(String name, String state, char sex, int age, int ID) {
					this.name = name;
					this.state = state;
					this.sex = sex;
					this.age = age;
					this.ID = ID;
				}

				public int getID() {
					return ID;
				}

				public String toString() {
					String record = name + " " + state + " " + sex + " " + age + " " + ID;
					return record;
				}

			}

	//CountingSort
			
	public static void CountingSort(ArrayList<Person> persons) {
		
		//Bestimmung der größten ID
		
		int maxID = 0;
		for (int i = 0; i < persons.size(); i++) {
			maxID = Math.max(maxID, persons.get(i).getID());
		}
		
		// Zählarray ( statt von 0 könnte man minID finden, das würde wahrscheinlich etwas schneller sein)
		
		int[] count = new int[maxID+1];
		
		for (int i = 0; i < persons.size(); i++) {
		    int id = persons.get(i).getID();
		    count[maxID-id]++; // Zählarray wird rückwarts angelegt
		}
	
		
		// Prefixsummenarray
		
		int[] prefixSum = new int[count.length]; 
		
		
		prefixSum[0] = count[0];
		for(int i = 1; i < count.length; i++) {
			prefixSum[i] = prefixSum[i-1] + count[i];
		}
		
		// PrefixArray umdrehen
		
		int[] prefixSumReverse = new int[prefixSum.length];
		for(int i = 0; i < prefixSum.length; i++) {
		    prefixSumReverse[i] = prefixSum[prefixSum.length-1-i];
		}
		
		// Speichern des umgedrehten Präfixsummenarrays in die Klassenvariable prefixSum
		
		CountingSort.prefixSum = new int[prefixSumReverse.length];
		for(int i=0; i < prefixSumReverse.length; i++) {
			CountingSort.prefixSum[i] = prefixSumReverse[i];
		}
		
		
		// Eigentliche Sortierung 
		
		// neuer Array um die sortierten Personen zu speichern
	    Person[] sortedPersons = new Person[persons.size()];
	    
	    // Iteriere von hinten nach vorne über die Personen (um Stabilität zu gewährleisten)
	    for (int i = persons.size()-1; i >= 0; i--) {
	        Person person = persons.get(i);
	        // Index in dem sortierten Person Array 
	        int index = prefixSumReverse[person.getID()] - 1;
	        sortedPersons[index] = person;
	        // dekrementiere den Wert an der Stelle prefixSumReverse[person.getID()] 
	        prefixSumReverse[person.getID()]--;
	    }
	    
		CountingSort.sortedPersons = sortedPersons; // Speichern als Klassenvariable um das Schreiben in die Ausgabedatei zu ermöglichen
		
	}
	
	public static void main(String args[]) {
		
		if (args.length < 2) 
			return;

		
		//Eingabedatei
		String inputFile = args[0];
		
		//Eingabedatei
		String outputFile = args[1];
		
		//Datenstruktur zum Verwalten der eingelesenen Elemente
		ArrayList<String> data = read(inputFile);
		
		//Personendaten
		ArrayList<Person> persons = new ArrayList<>();
		
		//Konvertierung der eingelesenen Zeichenketten in Personenobjekte (übernommen von Beispielcode)
		
				for (int i = 0; i < data.size(); i++) {

					String[] vpr = null;
					vpr = data.get(i).split("\\s+");

					String name, state;
					char sex;
					int age, ID;

					name = vpr[0];
					state = vpr[1];
					sex = vpr[2].charAt(0);
					age = Integer.parseInt(vpr[3]);
					ID = Integer.parseInt(vpr[4]);

					Person person = new Person(name, state, sex, age, ID);
					persons.add(person);
				}
		
		
		//Aufruf von CountingSort auf den Personendaten (absteigende Sortierung)
		CountingSort(persons);
		
		// schreiben der Prefixdatei
		try {		
			BufferedWriter writer = new BufferedWriter(new FileWriter("prefix.txt")); 
			
			for(int i=0; i < prefixSum.length;i++ ) {
				writer.write(Integer.toString(prefixSum[i]) + "\n");
			}

			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		

		//Herausschreiben der sortierten Personendaten in die Ausgabedatei
		write(outputFile, sortedPersons);
		
	}
}
