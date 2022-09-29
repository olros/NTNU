import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Exercise6 {
	public static void main(String[] args) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader("Java/vg2.txt"));
		WeightedGraph graph = new WeightedGraph(file);
		int startNode = 7;
		WNode node = graph.nodes[startNode];
		graph.dijkstra(node);
		String format = "%-6s%-8s%-8s%n";
		System.out.format(format, "Node", "From", "Distance");
		for (int i = 0; i < graph.amountOfNodes; i++) {
			if (((Previous) graph.nodes[i].data).distance != Previous.infinity) {
				String from = (graph.nodes[i].index == startNode) ? "Start" : String.valueOf(((Previous) graph.nodes[i].data).previousNode.index);
				System.out.format(format, graph.nodes[i].index, from, ((Previous) graph.nodes[i].data).distance);
			} else {
				System.out.format(format, graph.nodes[i].index, "", "Not reached");
			}

		}
	}
}

class WeightedGraph {
	final int amountOfNodes;
	WNode[] nodes;

	public WeightedGraph(BufferedReader file) throws IOException {
		StringTokenizer st = new StringTokenizer(file.readLine());
		int n = Integer.parseInt(st.nextToken());
		int vertexes = Integer.parseInt(st.nextToken());
		this.amountOfNodes = n;
		this.nodes = new WNode[amountOfNodes];
		for (int i = 0; i < amountOfNodes; ++i) nodes[i] = new WNode(i, i);
		for (int i = 0; i < vertexes; i++) {
			st = new StringTokenizer(file.readLine());
			int from = Integer.parseInt(st.nextToken());
			int to = Integer.parseInt(st.nextToken());
			int weight = Integer.parseInt(st.nextToken());
			nodes[from].vertex1 = new Vertex(nodes[to], nodes[from].vertex1, weight);
		}
	}

	void initPrevious(WNode startNode) {
		for (WNode node : nodes) {
			node.data = new Previous();
		}
		((Previous) startNode.data).distance = 0;
	}

	void createPriorityQueue(PriorityQueue<WNode> queue) {
		queue.addAll(Arrays.asList(nodes));
	}

	void shorten(WNode node, Vertex vertex, PriorityQueue<WNode> queue) {
		Previous nodeData = (Previous) node.data;
		Previous nextNodeData = (Previous) vertex.to.data;
		if (nextNodeData.distance > nodeData.distance + vertex.weight) {
			nextNodeData.distance = nodeData.distance + vertex.weight;
			nextNodeData.previousNode = node;
			queue.remove(vertex.to);
			queue.add(vertex.to);
		}
	}

	void dijkstra(WNode startNode) {
		initPrevious(startNode);
		PriorityQueue<WNode> priorityQueue = new PriorityQueue<>(this.amountOfNodes, Comparator.comparingInt(a -> ((Previous) a.data).distance));
		createPriorityQueue(priorityQueue);
		for (int i = amountOfNodes; i > 1; --i) {
			WNode node = priorityQueue.poll();
			for (Vertex vertex = node.vertex1; vertex != null; vertex = vertex.nextVertex) {
				shorten(node, vertex, priorityQueue);
			}
		}
	}
}

class WNode {
	Vertex vertex1;
	Object data;
	int index;
	int currentIndex;

	WNode(int index, int currentIndex) {
		this.index = index;
		this.currentIndex = currentIndex;
	}
}

class Vertex {
	Vertex nextVertex;
	WNode to;
	int weight;

	public Vertex(WNode to, Vertex nextVertex, int weight) {
		this.to = to;
		this.nextVertex = nextVertex;
		this.weight = weight;
	}
}

class Previous {
	static int infinity = 1000000000;
	int distance;
	WNode previousNode;

	Previous() {
		this.distance = infinity;
	}
}
