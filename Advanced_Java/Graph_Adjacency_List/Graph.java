package blatt6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Graph {

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

		//Funktion zum Schreiben in einen File
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

		//Knotenklasse
		private static class Node {

			int ID;
			float lat, lon;
			ArrayList<Edge> incomingEdges;
		    ArrayList<Edge> outgoingEdges;


			Node(int ID, float lat, float lon) {
				this.ID = ID;
				this.lat = lat;
				this.lon = lon;
				this.incomingEdges = new ArrayList<>();
		        this.outgoingEdges = new ArrayList<>();
			}

			public String toString() {
				String snode = "" + ID + " " + lat + " " + lon;
				return snode;
			}
		}


		//Kantenklasse
		private static class Edge {
			int s; //Startknoten
			int e; //Endknoten
			int g; //Gewicht

			Edge(int startknoten, int endknoten, int gewicht) {
				this.s = startknoten;
				this.e = endknoten;
				this.g = gewicht;
			}

			public String toString() {
				String sedge = "" + s + " " + e + " " + g;
				return sedge;
			}
		}
		
		//Graphklasse
		private static class G {
			
			Node[] nodes;


			//Konstruktor
			G(ArrayList<String> data) {
				
				//Einlesen der Knoten und Kanten
				int n = Integer.parseInt(data.get(0));
				int m = Integer.parseInt(data.get(1));
				
				nodes = new Node[n];
				
				
				for (int i = 2; i < n+2; i++) {
					String[] vpr = null;
					vpr = data.get(i).split("\\s+");
					float lat = Float.parseFloat(vpr[0]); // Latitude
					float lon = Float.parseFloat(vpr[1]); // Longtitude
					Node node = new Node(i-2,lat,lon);
					nodes[i-2] = node;
				}


				for (int i = n+2; i < n+m+2 ; i++) {
					String[] vpr = null;
					vpr = data.get(i).split("\\s+");
					int s, t, c;
					s = Integer.parseInt(vpr[0]);
					t = Integer.parseInt(vpr[1]);
					c = Integer.parseInt(vpr[2]);
					Edge edge = new Edge(s,t,c);
					nodes[s].outgoingEdges.add(edge); // Fuege dem Startknoten der Kante die Kante als ausgehende Kante hinzu
					nodes[t].incomingEdges.add(edge); // Fuege dem Endknoten der Kante die Kante als eingehende Kante hinzu
				}

				}
			
			/**
			 * Berechnet den Gesamtknotengrad eines Knotens als Summe der eingehenden und ausgehenden Kanten
			 * @param ID
			 * @return Knotengrad
			 */
			public int getDegree(int ID) {
				return nodes[ID].incomingEdges.size() + nodes[ID].outgoingEdges.size();
				
			}
			
			/**
			 * Gibt die Nachbarknoten eins Knotens aus (die durch ausgehende Kante von Startknoten mit ID verbunden sind)
			 * @param ID -  ID des Knotens 
			 * @return neighbours - Liste der Nachbarn
			 */
			public ArrayList<Node> getNeighbors(int ID) {
				ArrayList<Node> neighbours = new ArrayList<>();
				for(Edge e: nodes[ID].outgoingEdges) {
					int nodeID = e.e;
					neighbours.add(nodes[nodeID]);
	
				}
				return neighbours;
				
			}
			
			
			public ArrayList<Integer> getNeighborhood(int ID, int k) {
				
				ArrayList<Integer> neighbourhood = new ArrayList<>();
				
				// Wenn k == 0 gebe nur die ID zurück
				if (k == 0) {
					neighbourhood.add(ID);
					return neighbourhood;
				}
				
				// Wenn k == 1 gebe die Nachbarn des Knotens mit ID aus
				if (k == 1) {
					ArrayList<Node> neighbours = getNeighbors(ID);
					for(Node n: neighbours) {
					neighbourhood.add(n.ID);
					}
					return neighbourhood;
					
				}
			
				/**
				 *  wenn k >= 2 gebe die Menge der Nachbarn der Knoten aus dem Aufruf getNeighborhood(ID, k −1),
				 *  allerdings ohne Knoten aus den Mengen getNeighborhood(ID, k′) fur 0 ≤ k' ≤ k − 1. 
				 */
				ArrayList<Integer>  nachbarnderNachbarn = new ArrayList<>();
				
				for(int i: getNeighborhood(ID,k-1)) {
					for(Edge e: nodes[i].outgoingEdges) {
						nachbarnderNachbarn.add(e.e);
					}
				}
				
				ArrayList<Integer> loescheKnoten = new ArrayList<>();
				
				// hier wäre es vermutlich besser, Zwischenergebnisse zu speichern, statt die Funktion rekursiv immer wieder aufzurufen...
				for(int i = 0; i < k; i++) {
					ArrayList<Integer> neighbours = getNeighborhood(ID, i);
					for (int j: neighbours) {
						loescheKnoten.add(j);
					}
				}
						
				
				// Füge neighbourhood alle Knoten hinzu, die nicht in loescheKnoten und schon in der neighbourhood sind
				for(int i: nachbarnderNachbarn) {
					if(!loescheKnoten.contains(i) && !neighbourhood.contains(i)) {
				
					neighbourhood.add(i);
					}
					}
		        
				return neighbourhood;
			
			}
			
			}

		

		public static void main(String args[]) {

			
			if (args.length < 4)
				return;

			ArrayList<String> data = read(args[0]);
			
			int startID = Integer.parseInt(args[1]);
			
			int k = Integer.parseInt(args[2]);
			
			
			//Graph in Adjazenzlisten-Darstellung
			G graph = new G(data);

		
			//Berechne Knotengrade
			ArrayList<Integer> degrees = new ArrayList<>();

			for (int i = 0; i < graph.nodes.length; i++)
				degrees.add(graph.getDegree(i));
			
			// schreibe Knotengrade in Ausgabedatei1
			write(args[3], degrees);	
			
			
			
			
			// schreibe Nachbarn in Ausgabedatei2
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[4]))) {
			for(int i = 0; i < k+1; i++) {
				ArrayList<Integer> neighbourhood = graph.getNeighborhood(startID, i);
				StringBuilder sb = new StringBuilder();
		        for (int node : neighbourhood) {
		            sb.append(node).append(" ");
		        }
		        String line = sb.toString();
		        writer.write(line);
		        writer.newLine();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
				
		}
			
			
}
