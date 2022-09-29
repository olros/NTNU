import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Exercise5 {
	public static void main(String[] args) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader("Java/L7g2.txt"));
		Graph graph = graphFromFile(file);
		graph.printSCC();
	}

	static Graph graphFromFile(BufferedReader file) throws IOException {
		StringTokenizer st = new StringTokenizer(file.readLine());
		int nodes = Integer.parseInt(st.nextToken());
		int edges = Integer.parseInt(st.nextToken());
		Graph graph = new Graph(nodes);
		for (int i = 0; i < edges; i++) {
			st = new StringTokenizer(file.readLine());
			int from = Integer.parseInt(st.nextToken());
			int to = Integer.parseInt(st.nextToken());
			graph.addEdge(from, to);
		}
		return graph;
	}
}

class Graph {
	private final int amountOfNodes;
	private final LinkedList<Integer>[] nodes;

	// Create a graph with room for the given amount of nodes
	Graph(int amountOfNodes) {
		this.amountOfNodes = amountOfNodes;
		nodes = new LinkedList[amountOfNodes];
		for (int i = 0; i < amountOfNodes; ++i) {
			nodes[i] = new LinkedList<>();
		}
	}

	// Add edge
	public void addEdge(int from, int to) {
		nodes[from].add(to);
	}

	// Transpose the graph
	private Graph transpose() {
		Graph g = new Graph(amountOfNodes);
		for (int s = 0; s < amountOfNodes; s++) {
			for (Integer integer : nodes[s]) {
				g.nodes[integer].add(s);
			}
		}
		return g;
	}

	// Depth First Search
	// Traverses all unvisited nodes from the start node at index with recursion
	// When a nodes has no unvisited neighbors, it's added to the stack
	// If the deck is null then the node is printed out
	private void DFS(int index, boolean[] visitedNodes, Deque<Integer> deque) {
		visitedNodes[index] = true;
		for (int n : nodes[index]) {
			if (!visitedNodes[n]) {
				DFS(n, visitedNodes, deque);
			}
		}
		deque.push(index);
	}

	private void DFSPrint(int index, boolean[] visitedNodes, int component, ArrayList<String> components) {
		visitedNodes[index] = true;
//		System.out.print(index + " ");
		components.set(component, components.get(component) + index + " ");
		for (int n : nodes[index]) {
			if (!visitedNodes[n]) {
				DFSPrint(n, visitedNodes, component, components);
			}
		}
	}

	// Print strongly connected components
	public void printSCC() {
		// The deque will contain indexes of nodes in the graph in order from bottom to top
		Deque<Integer> deque = new ArrayDeque<>();

		// List containing status of whether a node has been visited
		boolean[] visitedNodes = new boolean[amountOfNodes];
		for (int i = 0; i < amountOfNodes; i++) visitedNodes[i] = false;

		// Do a DFS from all nodes which makes sure every node is visited
		for (int i = 0; i < amountOfNodes; i++) {
			if (!visitedNodes[i]) {
				DFS(i, visitedNodes, deque);
			}
		}

		// Transpose the graph
		Graph transposedGraph = transpose();

		// Set all indexes in visitedNodes to false so we can visit all of them again
		for (int i = 0; i < amountOfNodes; i++) visitedNodes[i] = false;

		// Keep track of components
		int component = 0;
		ArrayList<String> components = new ArrayList<>();

		// Traverse the deque as long as there is more nodes in it
		while (!deque.isEmpty()) {
			int s = deque.pop();
			// If the node hasn't been visited, it's part of a new component
			// and we'll do a DFS to find the other nodes in the component
			if (!visitedNodes[s]) {
				components.add((component + 1) + ": ");
				transposedGraph.DFSPrint(s, visitedNodes, component, components);
				component++;
			}
		}
		System.out.println("The graph has " + components.size() + " strongly connected components.");
		if (components.size() < 100) {
			components.forEach(System.out::println);
		}
	}
}