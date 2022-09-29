import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Exercise4a {
	public static void main(String[] args) {
		HashTable hashTable = new HashTable(87);
		try (Scanner scanner = new Scanner(new File("Java/navn.txt"))) {
			while (scanner.hasNextLine()) {
				hashTable.add(scanner.nextLine());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		System.out.println(hashTable.get("Olaf Rosendahl"));
		System.out.println(hashTable);
	}
}

class HashTable {
	private final int capacity;
	private final HashNode[] table;
	private int collisions = 0;
	private int indexesTaken = 0;

	public HashTable(int capacity) {
		this.capacity = capacity;
		this.table = new HashNode[capacity];
	}

	public void add(String value) {
		if (indexesTaken == capacity) {
			return;
		}
		int index = hash(value);
		HashNode newHashNode = new HashNode(value, null);
		if (table[index] == null) {
			table[index] = newHashNode;
			this.indexesTaken++;
		} else {
			HashNode currentNode = null;
			HashNode nextNode = table[index];
			while (nextNode != null) {
				if (nextNode.getValue().equals(value)) break;
				System.out.println("Collision: " + value + " -> " + nextNode.getValue());
				this.collisions++;
				currentNode = nextNode;
				nextNode = nextNode.getNext();
			}
			if (currentNode != null)
				currentNode.setNext(newHashNode);
		}
	}

	public HashNode get(String value) {
		int index = hash(value);
		System.out.println("\nGet " + value + ":");
		HashNode hashNode = table[index];
		while (hashNode != null) {
			if (hashNode.getValue().equals(value)) {
				break;
			}
			System.out.println("Collision: " + value + " -> " + hashNode.getValue());
			hashNode = hashNode.getNext();
		}
		return hashNode;
	}

	private int hash(String text) {
		return Math.floorMod(stringToInt(text), capacity);
	}

	private int stringToInt(String text) {
		int hash = 7;
		for (int i = 0; i < text.length(); i++) {
			hash = hash * 31 + text.charAt(i);
		}
		return hash;
	}

	@Override
	public String toString() {
		return "HashTable stats:\n" +
				"- Capacity=" + capacity + "\n" +
				"- Collisions=" + collisions + "\n" +
				"- Indexes taken=" + indexesTaken + "\n" +
				"- Load factor=" + ((double)indexesTaken / capacity) + "\n";
	}
}

class HashNode {
	private final String value;
	private HashNode next;

	public HashNode(String value, HashNode next) {
		this.value = value;
		this.next = next;
	}

	public String getValue() {
		return value;
	}

	public HashNode getNext() {
		return next;
	}

	public void setNext(HashNode next) {
		this.next = next;
	}

	@Override
	public String toString() {
		return "HashNode: " + value + "\n";
	}
}
