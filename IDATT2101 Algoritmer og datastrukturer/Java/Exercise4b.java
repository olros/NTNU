import java.util.HashMap;

public class Exercise4b {
	public static void main(String[] args) {
		int[] randomNumbers = new int[10000000];
		for (int i = 0; i < randomNumbers.length; i++) {
			randomNumbers[i] = (int) (Math.random() * randomNumbers.length * 10);
		}
		HugeHashTable hTable = new HugeHashTable(randomNumbers.length);
		long totalTime = 0;
		for (int i = 0; i < randomNumbers.length; i++) {
			long start = System.nanoTime();
			hTable.add(randomNumbers[i]);
			totalTime += System.nanoTime() - start;
		}
		System.out.println("Custom HashMap: " + totalTime / 1000000 + " millisecs");
		System.out.println(hTable);
		HashMap<Integer, Integer> hashMap = new HashMap<>(randomNumbers.length);
		totalTime = 0;
		for (int i = 0; i < randomNumbers.length; i++) {
			long start = System.nanoTime();
			hashMap.put(randomNumbers[i], randomNumbers[i]);
			totalTime += System.nanoTime() - start;
		}
		System.out.println("Java HashMap: " + totalTime / 1000000 + " millisecs");
	}
}

class HugeHashTable {
	private static final int PRIME = 7;
	private final int[] table;
	private int collisions = 0;
	private int indexesTaken = 0;

	public HugeHashTable(int capacity) {
		this.table = new int[getLength(capacity, 1.17)];
	}

	private boolean checkIsPrime(int k) {
		boolean isPrime = true;
		for (int i = 2; i < k / 2; i++) {
			if ((k % i) == 0) {
				isPrime = false;
			}
		}
		return isPrime;
	}

	private int getLength(int length, double times) {
		int num = (int) (length * times);
		boolean isPrime = false;
		while (!isPrime) {
			isPrime = checkIsPrime(num);
			num++;
		}
		return isPrime ? num - 1 : -1;
	}

	public void add(int number) {
		if (indexesTaken == table.length) {
			return;
		}
		int index = hash1(number);
		if (table[index] != 0) {
			int index2 = hash2(number);
			while (table[index] != 0) {
				index = (index + index2) % table.length;
				collisions++;
			}
		}
    table[index] = number;
		indexesTaken++;
	}

	public int get(int number) {
		int index = hash1(number);
		if (table[index] == number) {
			return table[index];
		} else {
			int index2 = hash2(number);
			while (true) {
				index = (index + index2) % table.length;
				collisions++;
				if (table[index] == number) {
					return table[index];
				}
			}
		}
	}

	private int hash1(int number) {
		return number % table.length;
	}

	private int hash2(int number) {
		return (PRIME - (number % PRIME));
	}

	@Override
	public String toString() {
		return "Custom HashMap stats:\n" +
				"- Capacity = " + table.length + "\n" +
				"- Collisions = " + collisions + "\n" +
				"- Indexes taken = " + indexesTaken + "\n" +
				"- Load factor = " + ((double) indexesTaken / table.length) + "\n";
	}
}
