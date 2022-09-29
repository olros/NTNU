import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

public class Exercise3b {

	public static void main(String[] args) {
		Exercise3b e = new Exercise3b();
		e.run();
	}

	public void run() {
		System.out.println("Enter the strings separated by spaces");
		System.out.println("To exit, press enter\n");
		Scanner in = new Scanner(System.in);
		boolean isFinished = false;
		while (!isFinished) {
			System.out.println("Enter strings:");
			String input = in.nextLine();
			String[] split = input.trim().split("\\s+");
			if (split.length > 0) {
				Tree tree = new Tree();
				Arrays.stream(split).forEach(s -> tree.add(s));
				tree.printLevelOrder();
			} else {
				System.out.println("Exits...");
				isFinished = true;
			}
		}
	}
}

class Tree {
	TreeNode root;

	public Tree() {
		root = null;
	}

	static int findHeight(TreeNode root) {
		return root == null ? 0 : 1 + Math.max(
				findHeight(root.left), findHeight(root.right)
		);
	}

	void printLevelOrder() {
		LinkedList<QueueItem> queue = new LinkedList<>();
		ArrayList<TreeNode> level = new ArrayList<>();
		int depth = findHeight(root);
		queue.add(new QueueItem(root, depth));

		for (;;) {
			QueueItem curr = queue.poll();
			if (curr.depth < depth) {
				depth = curr.depth;

				for (int i = (int)Math.pow(2, depth) - 1; i > 0; i--) {
					System.out.print(" ");
				}
				for (TreeNode n : level) {
					System.out.print(n == null ? " " : n.element);
					for (int i = (int)Math.pow(2, depth + 1); i > 1; i--) {
						System.out.print(" ");
					}
				}

				System.out.println();
				level.clear();
				if (curr.depth <= 0) break;
			}

			level.add(curr.node);
			if (curr.node == null) {
				queue.add(new QueueItem(null, depth - 1));
				queue.add(new QueueItem(null, depth - 1));
			} else {
				queue.add(new QueueItem(curr.node.left, depth - 1));
				queue.add(new QueueItem(curr.node.right, depth - 1));
			}
		}
	}

	public void add(String elem) {
		if (root == null) {
			root = new TreeNode(elem, null, null, null);
			return;
		}
		TreeNode treeNode = root;
		String compare = null;
		TreeNode parent = null;
		while (treeNode != null) {
			parent = treeNode;
			compare = treeNode.element;
			if (elem.compareToIgnoreCase(compare) <= 1) treeNode = treeNode.left;
			else treeNode = treeNode.right;
		}
		if (elem.compareToIgnoreCase(compare) <= 1) parent.left = new TreeNode(elem, parent, null, null);
		else parent.right = new TreeNode(elem, parent, null, null);
	}

	public boolean empty() {
		return root == null;
	}
}

class TreeNode {
	String element;
	TreeNode parent;
	TreeNode left;
	TreeNode right;

	public TreeNode(String element, TreeNode parent, TreeNode left, TreeNode right) {
		this.element = element;
		this.parent = parent;
		this.left = left;
		this.right = right;
	}
}

class QueueItem {
	TreeNode node;
	int depth;

	public QueueItem(TreeNode node, int depth) {
		this.node = node;
		this.depth = depth;
	}
}
