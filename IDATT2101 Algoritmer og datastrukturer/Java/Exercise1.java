import java.util.function.BiFunction;

public class Exercise1 {
	// How many seconds each time test should run
	private static final int SECONDS_PER_TIME_TEST = 1;
	// Used to format output as tables
	private static final String CHECK_COLUMNS_FORMAT = "%-20s%-10s%-10s%-10s%n";
	private static final String TIME_COLUMNS_FORMAT = "%-8s%-15s%-15s%n";

	// Complexity O(n)
	public static double pow1(double x, int n) {
		if (n == 0) return 1.0;
		else return x * pow1(x, n -1);
	}

	// Complexity O(log(n))
	public static double pow2(double x, int n) {
		if (n == 0) return 1.0;
		else if (n % 2 == 1) return x * pow2(x * x, (n - 1) / 2);
		else return pow2(x * x, n / 2);
	}

	// Complexity O(1)
	public static double pow3(double x, int n) {
		return Math.pow(x, n);
	}

	// Check if all algorithms calculates correctly
	public static void checkCorrect(double x, int n, double answer) {
		System.out.format(CHECK_COLUMNS_FORMAT, x + "^" + n + "=" + answer, pow1(x, n) == answer, pow2(x, n) == answer, pow3(x, n) == answer);
	}

	// Test how many times a function can run per second with the given x and n
	public static void testTime(BiFunction<Double, Integer, Double> func, double x, int n) {
		int count = 0;
		long startTime = System.nanoTime();
		while(System.nanoTime() - startTime < 1000000000.0 * SECONDS_PER_TIME_TEST) {
			func.apply(x, n);
			count++;
		}
		System.out.format(TIME_COLUMNS_FORMAT, n, count / SECONDS_PER_TIME_TEST, Math.round(1000000000.0 * SECONDS_PER_TIME_TEST / count) + " nanosecs.");
	}

	public static void main(String[] args) {
		System.out.println("--- Check that the functions calculates correctly ---");
		System.out.format(CHECK_COLUMNS_FORMAT, "Calculation", "Func 1", "Func 2", "Func 3");
		checkCorrect(3, 14, 4782969);
		checkCorrect(7, 4, 2401);
		checkCorrect(2, 10, 1024);
		double x = 2;
		System.out.println("\n--- Count how many times the functions can run in a second ---");
		System.out.println("Task 1 - O(n):");
		System.out.format(TIME_COLUMNS_FORMAT, "N", "Runs per sec.", "Time per run");
		testTime(Exercise1::pow1, x, 10);
		testTime(Exercise1::pow1, x, 100);
		testTime(Exercise1::pow1, x, 1000);
		testTime(Exercise1::pow1, x, 10000);

		System.out.println("\nTask 2 - O(log n):");
		System.out.format(TIME_COLUMNS_FORMAT, "N", "Runs per sec.", "Time per run");
		testTime(Exercise1::pow2, x, 10);
		testTime(Exercise1::pow2, x, 100);
		testTime(Exercise1::pow2, x, 1000);
		testTime(Exercise1::pow2, x, 10000);

		System.out.println("\nTask 3 - O(1):");
		System.out.format(TIME_COLUMNS_FORMAT, "N", "Runs per sec.", "Time per run");
		testTime(Exercise1::pow3, x, 10);
		testTime(Exercise1::pow3, x, 100);
		testTime(Exercise1::pow3, x, 1000);
		testTime(Exercise1::pow3, x, 10000);
	}
}