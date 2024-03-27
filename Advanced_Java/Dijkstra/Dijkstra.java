package blatt7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;



public class Dijkstra {

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
					int numNodes;
					int numEdges;


					//Konstruktor
					G(ArrayList<String> data) {
						
						//Einlesen der Knoten und Kanten
						int n = Integer.parseInt(data.get(0));
						int m = Integer.parseInt(data.get(1));
						
						this.numNodes = n;
						this.numEdges = m;
						
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
					
					
					
				}
				
			
			// Nodes für den MinHeap
			private static class NodeH {
				Node content; // Knoten an sich
				int distance; // Verwendet als Schlüssel
				
				// Konstruktor
				NodeH(Node node, int distance) {
					this.content = node;
					this.distance = distance; 
					
				}
				
			}
			
			// MinHeap Implementation
			private static class MinHeap {
				ArrayList<NodeH> heap = new ArrayList<>();
			 
				// Konstruktor
				MinHeap(Node n, int distance) { 
					NodeH root = new NodeH(n, distance); // 
					heap.add(root);
				}
			
				
			public void insertNode(Node n, int distance) {
				NodeH newNode = new NodeH(n, distance); 
				heap.add(newNode); // füge neuen Knoten ganz unten ein in den Heap
				
			}
			
			
			/**
			 * Die Funktion iteriert über alle Parentknoten und befördert den Knoten mit dem kleinsten Distanzwert an Position 0 der ArrayList heap.
			 * (Die Heapeigenschaft wird dadurch nicht gewährleistet, allerdings ist für die PQ des Dijkstra-Algorithmus nur wichtig, dass das Minimum extrahiert wird.
			 * Da die Laufzeit für die Final Runs extrem hoch ist, könnte es sein, dass bei dieser Funktion ein Fehler vorliegt oder der minHeap anders implementiert werden müsste.
			 * Es könnte auch ein Fehler in der Implementierung des Dijkstra-Algorithmus vorliegen (?)
			 * 
			 */
			public void heapify() { 
				
				if(heap.size() == 1) { // Minimum ist schon an Position 0
					return;
				}
				
				int startIndex = (int)(heap.size() - 2) /2; // Start index = Index des ersten Parents
				
				if(heap.size() == 2) { // Bei heap.size() = 2 gibt es nur ein linkes Kind
					int child = 1;
					if(heap.get(child).distance < heap.get(startIndex).distance) { // Wenn Distanz des Kindes kleiner ist, tausche nach oben
						NodeH temp = heap.get(startIndex);
						heap.set(startIndex, heap.get(child));
						heap.set(child, temp);
						return;
					}
				}
				
				
				for (int i = startIndex; i >= 0; i--) { 
					int leftChild = 2 * i+1; // Position des linken Kindes
					int rightChild = 2 * i+2; // Position des rechten Kindes
					int smallest; // smallest of left and right child
					if(rightChild > heap.size()-1) { // wenn Position des rechten Kindes größer ist als Länge der Liste
						smallest = leftChild;
					}
					else { // wenn es rechtes Kind gibt
						if(heap.get(leftChild).distance < heap.get(rightChild).distance) {
							smallest = leftChild;
						}
						else {
							smallest = rightChild;
						}
					}
			
					// wenn das kleinere Kind kleiner als aktueller Knoten ist, tausche kleines Kind nach oben
					if(heap.get(smallest).distance < heap.get(i).distance) {
						NodeH temp = heap.get(i);
						heap.set(i, heap.get(smallest));
						heap.set(smallest, temp);
					}
					
				}
				
				}
			
			/**
			 * Gibt den Knoten mit der kleinsten Distanz zurück.
			 * @return Knoten mit der kleinsten Distanz.
			 */
			public Node extractMin() {
				heapify(); // Befördere Minimum nach oben
				Node min = heap.get(0).content; // Minimum
				heap.set(0, this.heap.get(heap.size()-1)); // überschreibe Minimum mit Element an letzter Position
				this.heap.remove(heap.size()-1); // entferne letztes Element, das jetzt an Position 0 ist
				return min; // Gebe Knoten mit kleinstem Distanzwert zurück
				
			}
			}
			
			/**
			 * Berechnet die kürzeste Distanz zwischen zwei Knoten.
			 * @param graph - Graph in dem gesucht werden soll
			 * @param startNode - Startknoten
			 * @param endNode - Endknoten
			 * @return distance - die kürzeste Distanz der beiden Knoten
			 */
			public static int dijkstraAlgorithm(G graph, Node startNode, Node endNode) {
				
			ArrayList<Integer> d = new ArrayList<>(); // Distanzarray
		
			for(int i = 0; i < graph.numNodes; i++) {
				d.add(Integer.MAX_VALUE); // Distanzarray auf Unendlich setzen
			}
			
			
			// setze Distanz des Startknotens auf 0
			d.set(startNode.ID, 0);
			
			
			ArrayList<Integer> vor = new ArrayList<>();// Vorgängerarray, alle Werte auf -1
			
			for(int i = 0; i < graph.numNodes; i++) {
				vor.add(-1);
			}
			
			// erstelle PQ mit Startknoten, der Distanz 0 hat
			MinHeap pq = new MinHeap(startNode, 0);
			
			
			while(pq.heap.size() != 0) { // solange heap bzw. PQ nicht leer ist
				
				Node v = pq.extractMin(); // Minimum
				
				// iteriere über alle ausgehenden Kanten von v
				for(Edge e: v.outgoingEdges) {
					// wenn Distanz des Endknotens (ID = e.e) größer ist als Distanz des Startknotens + Gewicht der Kante (e.g)
					// füge der pq den Knoten mit verringerter Distanz hinzu bzw. neu hinzu
					if(d.get(e.e) > (d.get(v.ID) + e.g)) {
						d.set(e.e, (d.get(v.ID) + e.g));
						vor.set(e.e, v.ID); // setze Vorgängerarray an Stelle des Endknotens auf v
						pq.insertNode(graph.nodes[e.e], d.get(e.e)); 
						
					}
				}
				
			}
			
			
			int distance = d.get(endNode.ID); // Distanz von Startknoten zu Endknoten in Distanzarray ablesen
			if (distance == Integer.MAX_VALUE) { // Wenn es keinen Weg von Start zu Endknoten gibt wird -1 ausgegeben
				return -1;
			}
			return distance; 
			}

		public static void main(String args[]) {

			if (args.length < 3)
				return;
			
			// Graph einlesen 
			ArrayList<String> data = read(args[0]);
	
			// Graph in Adjazenzlisten-Darstellung
			G graph = new G(data);
			
			// Start- und Zielknoten einlesen (Knoten IDs)
			ArrayList<String> st = read(args[1]);
			
			ArrayList<Integer> distances = new ArrayList<>();
			
			for (int i = 0; i < st.size(); i++) {
				String[] vpr = null;
				vpr = st.get(i).split("\\s+");
				int s = Integer.parseInt(vpr[0]);
				int t = Integer.parseInt(vpr[1]);
				
				System.out.println(i);
				int dist = dijkstraAlgorithm(graph,graph.nodes[s], graph.nodes[t]);

				System.out.println("i: " + s + " --> " + t + "   dist = " + dist); 
				distances.add(dist);			
			}
			
			// schreibe Distanzen in Ausgabefile
			write(args[2], distances);
			
		}
			
}	
		


