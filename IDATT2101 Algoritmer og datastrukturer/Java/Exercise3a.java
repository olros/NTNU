import java.util.Arrays;
import java.util.Scanner;

public class Exercise3a {

	public static void main(String[] args) {
		System.out.println("To calculate, enter two numbers with spaces and either + or - in between. (Ex.: 45 + 32)");
		System.out.println("To exit, press enter\n");
		Scanner in = new Scanner(System.in);
		boolean isFinished = false;
		while (!isFinished) {
			System.out.println("Enter numbers to calculate:");
			String input = in.nextLine();
			String[] split = input.trim().split("\\s+");
			if (split.length == 3) {
				calculate(split);
			} else {
				System.out.println("Exits...");
				isFinished = true;
			}
		}
	}

	public static void calculate(String[] input) {
		if (input.length != 3) throw new IllegalArgumentException("The input must contain 3 strings");
		int maxLength = input[0].length();
		if (input[2].length() > maxLength) maxLength = input[2].length();
		DoubleLinkedList list1 = createListFromString(input[0], maxLength);
		DoubleLinkedList list2 = createListFromString(input[2], maxLength);
		DoubleLinkedList result = new DoubleLinkedList();
		int rest = 0;
		int count = 0;
		Node node1 = list1.getTail();
		Node node2 = list2.getTail();

		while (count != list1.getNoOfElements()) {
			rest = calc(result, input[1], node1 != null ? node1.getElement() : 0, node2 != null ? node2.getElement() : 0, rest);
			node1 = node1.getPrevious();
			node2 = node2 != null ? node2.getPrevious() : null;
			count++;
		}
		System.out.format("%-3s%-" + result.getNoOfElements() +"s%n", "", list1);
		System.out.format("%-3s%-" + result.getNoOfElements() +"s%n", input[1], list2);
		System.out.format("%-3s%-" + result.getNoOfElements() +"s%n", "=", result);
	}

	public static DoubleLinkedList createListFromString(String input, int length) {
		DoubleLinkedList list = new DoubleLinkedList();
		Arrays.stream(input.split("")).forEach(s -> list.insertLast(Integer.parseInt(s)));
		while (list.getNoOfElements() < length) {
			list.insertFirst(0);
		}
		return list;
	}

	public static int calc(DoubleLinkedList list, String operator, int first, int last, int rest) {
		if (operator.equals("+")) {
			int value = first + last + rest;
			if (value < 10) {
				list.insertFirst(value);
				return 0;
			} else {
				list.insertFirst(value - 10);
				return 1;
			}
		} else {
			int value = first + rest - last;
			if (value < 0) {
				list.insertFirst(10 + value);
				return -1;
			} else {
				list.insertFirst(value);
				return 0;
			}
		}
	}
}

class DoubleLinkedList {
	private Node head = null;
	private Node tail = null;
	private int noOfElements = 0;

	public int getNoOfElements() {
		return this.noOfElements;
	}
	public Node getHead() {
		return this.head;
	}
	public Node getTail() {
		return this.tail;
	}

	public void insertFirst(int value) {
		head = new Node(value, head, null);
		if (tail == null) tail = head;
		else head.next.previous = head;
		++noOfElements;
	}
	public void insertLast(int value) {
		Node newNode = new Node(value, null, tail);
		if (tail != null) tail.next = newNode;
		else head = newNode;
		tail = newNode;
		++noOfElements;
	}

	@Override
	public String toString() {
		Node elem = getHead();
		String text = elem.getElement() != 0 ? String.valueOf(elem.getElement()) : "";
		boolean isFinished = false;
		while (!isFinished) {
			elem = elem.getNext();
			text = text + ((elem.getElement() != 0 || (elem.getElement() == 0 && !text.equals(""))) ? String.valueOf(elem.getElement()) : "");
			if (elem.getNext() == null) isFinished = true;
		}
		return text.equals("") ? "0" : text;
	}
}

class Node {
	int element;
	Node next;
	Node previous;

	Node (int element, Node next, Node previous) {
		this.element = element;
		this.next = next;
		this.previous = previous;
	}

	public int getElement() {
		return element;
	}

	public Node getNext() {
		return next;
	}

	public Node getPrevious() {
		return previous;
	}
}
