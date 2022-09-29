import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Exercise8 {
	public static void main(String[] args) {
		try (
				BufferedReader nodeFile = new BufferedReader(new FileReader("noder.txt"));
				BufferedReader edgeFile = new BufferedReader(new FileReader("kanter.txt"));
				BufferedReader POIFile = new BufferedReader(new FileReader("interessepkt.txt"))
		) {
			Graph graph = new Graph(nodeFile, edgeFile, POIFile);
			Scanner in = new Scanner(System.in);
			while (true) {
				System.out.println("\n**** Options: ****");
				System.out.println("Nearest by type: 1");
				System.out.println("Route dijkstra: 2");
				System.out.println("Route A*: 3");
				System.out.println("End program: -1");

				System.out.print("What to do: ");
				int toDo = in.nextInt();
				if (toDo == 1) {
					in.nextLine();
					System.out.print("Around where: ");
					Integer place = graph.places.get(in.nextLine());
					System.out.print("Type: ");
					int type = in.nextInt();
					if (place == null) {
						System.out.println("Couldn't find the place");
						continue;
					}
					graph.findNearestByTypeWithDijkstra(place, type);
				} else if (toDo == 2 || toDo == 3) {
					in.nextLine();
					System.out.print("From where: ");
					Integer from = graph.places.get(in.nextLine());
					System.out.print("To where: ");
					Integer to = graph.places.get(in.nextLine());
					if (from == null || to == null) {
						System.out.println("Couldn't find the places");
						continue;
					}
					System.out.println("Route: ");
					if (toDo == 2) {
						graph.findRouteWithDijkstra(from, to);
					} else {
						graph.findRouteWithAstar(from, to);
					}
				} else if (toDo == -1) {
					System.out.println("Exits...");
					break;
				}
				graph.reset();
			}
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't read some of the files");
		}

	}
}

class Graph {
	Node[] nodes;
	HashMap<String, Integer> places = new HashMap<>();
	int amountOfNodes;

	public Graph(BufferedReader nodesFile, BufferedReader edgesFiles, BufferedReader POIsFile) throws IOException {
		readNodes(nodesFile);
		readEdges(edgesFiles);
		readPOIs(POIsFile);
	}

	// Reset the nodes to prepare for a new run
	public void reset() {
		for (Node node : this.nodes) {
			node.endNode = false;
			node.data = new Previous();
			node.visited = false;
		}
	}

	private void readNodes(BufferedReader nodesFile) throws IOException {
		StringTokenizer st = new StringTokenizer(nodesFile.readLine());
		this.amountOfNodes = Integer.parseInt(st.nextToken());
		this.nodes = new Node[amountOfNodes];
		for (int i = 0; i < amountOfNodes; ++i) {
			st = new StringTokenizer(nodesFile.readLine());
			int index = Integer.parseInt(st.nextToken());
			double lat = Double.parseDouble(st.nextToken()) * (Math.PI / 180);
			double lon = Double.parseDouble(st.nextToken()) * (Math.PI / 180);
			nodes[index] = new Node(index, lat, lon);
			nodes[index].data = new Previous();
			nodes[index].cosLat = Math.cos(lat);
		}
	}

	private void readEdges(BufferedReader edgesFile) throws IOException {
		StringTokenizer st = new StringTokenizer(edgesFile.readLine());
		int amount = Integer.parseInt(st.nextToken());
		for (int i = 0; i < amount; ++i) {
			st = new StringTokenizer(edgesFile.readLine());
			int from = Integer.parseInt(st.nextToken());
			int to = Integer.parseInt(st.nextToken());
			int weight = Integer.parseInt(st.nextToken());
			int length = Integer.parseInt(st.nextToken());
			int speedlimit = Integer.parseInt(st.nextToken());
			nodes[from].firstEdge = new Edge(nodes[to], nodes[from].firstEdge, weight, speedlimit, length);
		}
	}

	private void readPOIs(BufferedReader POIsFile) throws IOException {
		StringTokenizer st = new StringTokenizer(POIsFile.readLine());
		int m = Integer.parseInt(st.nextToken());
		for (int i = 0; i < m; ++i) {
			st = new StringTokenizer(POIsFile.readLine());
			int n = Integer.parseInt(st.nextToken());
			int type = Integer.parseInt(st.nextToken());
			String name = st.nextToken();
			while (st.hasMoreTokens()) name += " " + st.nextToken();
			this.nodes[n].type = type;
			this.nodes[n].name = name;
			this.places.put(name, n);
		}
	}

	private int findDistance(Node node1, Node node2) {
		double sinLat = Math.sin((node1.latitude - node2.latitude) / 2.0);
		double sinLng = Math.sin((node1.longitude - node2.longitude) / 2.0);
		return (int) (35285538.46153846153846153846 * Math.asin(Math.sqrt(
				sinLat * sinLat + node1.cosLat * node2.cosLat * sinLng * sinLng)));
	}

	private PriorityQueue<Node> getDijkstraPriorityQueue() {
		return new PriorityQueue<>(Comparator.comparingInt(a -> (a.data.distance)));
	}

	private PriorityQueue<Node> getAstarPriorityQueue() {
		return new PriorityQueue<>(Comparator.comparingInt(a -> (a.data.fullDistance)));
	}

	private Node[] dijkstraNearestByType(Node startNode, int type) {
		startNode.data.distance = 0;
		PriorityQueue<Node> queue = getDijkstraPriorityQueue();
		queue.add(startNode);
		Node[] nearest = new Node[10];
		int noFound = 0;
		for (int i = this.amountOfNodes; i > 1 && !queue.isEmpty(); --i) {
			Node node = queue.poll();
			if (!node.visited && (node.type == type || ((type == 2 || type == 4) && node.type == 6))) {
				nearest[noFound] = node;
				noFound++;
				node.visited = true;
			}
			if (noFound == 10) break;
			for (Edge edge = node.firstEdge; edge != null; edge = edge.nextEdge) {
				shorten(node, edge, queue);
			}
		}
		return nearest;
	}

	public void findNearestByTypeWithDijkstra(int startNodeNr, int type) {
		Node[] nearestNodes = dijkstraNearestByType(this.nodes[startNodeNr], type);
		for (Node node : nearestNodes) {
			if (node != null) System.out.println(node.name + " " + node.type);
		}
		System.out.println("Locations: ");
		for (Node node : nearestNodes) {
			if (node != null)
				System.out.println(node.latitude * (180 / Math.PI) + ", " + node.longitude * (180 / Math.PI));
		}
	}

	private int dijkstraFromTo(Node startNode, Node endNode) {
		startNode.data.distance = 0;
		endNode.endNode = true;
		PriorityQueue<Node> queue = getDijkstraPriorityQueue();
		queue.add(startNode);
		int count = 0;
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			count++;
			if (node.endNode) return count;
			for (Edge edge = node.firstEdge; edge != null; edge = edge.nextEdge) {
				shorten(node, edge, queue);
			}
		}
		return -1;
	}

	private int astarFromTo(Node startNode, Node endNode) {
		startNode.data.distance = 0;
		startNode.data.distanceToEnd = findDistance(startNode, endNode);
		startNode.data.fullDistance = startNode.data.distanceToEnd;
		endNode.endNode = true;
		PriorityQueue<Node> queue = getAstarPriorityQueue();
		queue.add(startNode);
		int count = 0;
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			count++;
			if (node.endNode) return count;
			for (Edge edge = node.firstEdge; edge != null; edge = edge.nextEdge) {
				shorten(node, edge, endNode, queue);
			}
		}
		return -1;
	}

	public void findRouteWithDijkstra(int from, int to) {
		Node startNode = this.nodes[from];
		Node endNode = this.nodes[to];
		long startTime = System.nanoTime();
		int checked = dijkstraFromTo(startNode, endNode);
		long time = System.nanoTime() - startTime;
		System.out.println("Dijkstra: " + checked + " nodes checked, " + (double) time / 1000000 + "ms taken.");
		printRoute(startNode, endNode);
	}

	public void findRouteWithAstar(int from, int to) {
		Node startNode = this.nodes[from];
		Node endNode = this.nodes[to];
		long startTime = System.nanoTime();
		int checked = astarFromTo(startNode, endNode);
		long time = System.nanoTime() - startTime;
		System.out.println("Astar: " + checked + " nodes checked, " + (double) time / 1000000 + "ms taken.");
		printRoute(startNode, endNode);
	}

	private void printRoute(Node startNode, Node endNode) {
		String startName = startNode.name.replaceAll("[^a-zA-Z0-9]", "");
		String endName = endNode.name.replaceAll("[^a-zA-Z0-9]", "");
		String fileName = startName + "-" + endName + ".txt";
		try (FileWriter outputStream = new FileWriter(fileName)) {
			Node node = endNode;
			int milliseconds = node.data.distance * 10;
			int seconds = (milliseconds / 1000) % 60;
			int minutes = ((milliseconds / (1000 * 60)) % 60);
			int hours = ((milliseconds / (1000 * 60 * 60)) % 24);
			System.out.println("Time: " + String.format("%02d hours, %02d min, %02d sec", hours, minutes, seconds));
			while (node != null) {
				outputStream.write(node.toString() + "\n");
				node = node.data.previousNode;
			}
			System.out.println("The route has been printed to the file: " + fileName);
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't find/print route.");
		}
	}

	void shorten(Node node, Edge edge, PriorityQueue<Node> queue) {
		Previous nodeData = node.data;
		Previous nextNodeData = edge.to.data;
		if (nextNodeData.distance > nodeData.distance + edge.time) {
			nextNodeData.distance = nodeData.distance + edge.time;
			nextNodeData.previousNode = node;
			queue.add(edge.to);
		}
	}

	void shorten(Node startNode, Edge edge, Node endNode, PriorityQueue<Node> queue) {
		Previous startPrevious = startNode.data;
		Previous endPrevious = edge.to.data;
		if (endPrevious.distanceToEnd == -1) {
			int dist = findDistance(edge.to, endNode);
			endPrevious.distanceToEnd = dist;
			endPrevious.fullDistance = dist + endPrevious.distance;
		}
		if (endPrevious.distance > startPrevious.distance + edge.time) {
			endPrevious.distance = startPrevious.distance + edge.time;
			endPrevious.previousNode = startNode;
			endPrevious.fullDistance = endPrevious.distance + endPrevious.distanceToEnd;
			queue.add(edge.to);
		}
	}
}

class Node {
	Edge firstEdge;
	Previous data;
	int index;
	double latitude;
	double longitude;
	double cosLat;
	boolean visited;
	boolean endNode;
	int type;
	String name;

	Node(int index, double latitude, double longitude) {
		this.index = index;
		this.latitude = latitude;
		this.longitude = longitude;
		this.cosLat = Math.cos(latitude);
		this.visited = false;
		this.name = "";
	}

	@Override
	public String toString() {
		return latitude * (180 / Math.PI) + ", " + longitude * (180 / Math.PI) + ", " + name;
	}
}

class Edge {
	Edge nextEdge;
	Node to;
	int time;
	int distance;
	int speedLimit;

	public Edge(Node to, Edge nextEdge, int time, int distance, int speedLimit) {
		this.to = to;
		this.nextEdge = nextEdge;
		this.time = time;
		this.distance = distance;
		this.speedLimit = speedLimit;
	}
}

class Previous {
	static int infinity = 1000000000;
	int distance;
	int fullDistance;
	int distanceToEnd;
	Node previousNode;

	Previous() {
		this.distance = infinity;
		this.fullDistance = infinity;
		this.distanceToEnd = -1;
	}
}
