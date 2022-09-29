import java.util.Arrays;

public class Exercise2 {
	// Used to format output as tables
	private static final String TIME_COLUMNS_FORMAT = "%-15s%-9s%-7b%-7b%n";

	// Swap to indexes in an array
	private static void swap(int[] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	// Dual pivot Quicksort helper method
	private static int[] partition(int[] arr, int low, int high) {
		swap(arr, low, low + (high - low) / 3);
		swap(arr, high, high - (high - low) / 3);
		if (arr[low] > arr[high]) swap(arr, low, high);

		int j = low + 1;
		int g = high - 1;
		int k = low + 1;
		int p = arr[low];
		int q = arr[high];

		while (k <= g) {
			if (arr[k] < p) {
				swap(arr, k, j);
				j++;
			}
			else if (arr[k] >= q) {
				while (arr[g] > q && k < g)
					g--;

				swap(arr, k, g);
				g--;
				if (arr[k] < p) {
					swap(arr, k, j);
					j++;
				}
			}
			k++;
		}
		j--;
		g++;
		swap(arr, low, j);
		swap(arr, high, g);
		return new int[] {j, g};
	}

	// Dual pivot Quicksort method
	public static void dualPivotQuickSort(int[] arr, int low, int high) {
		if (low < high) {
			int[] piv = partition(arr, low, high);

			dualPivotQuickSort(arr, low, piv[0] - 1);
			dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
			dualPivotQuickSort(arr, piv[1] + 1, high);
		}
	}

	// Quicksort helper method
	private static int split(int[] arr, int left, int right) {
		int iLeft;
		int iRight;
		int medianIndex = median3sort(arr, left, right);
		int medianNumber = arr[medianIndex];
		swap(arr, medianIndex, right - 1);
		for (iLeft = left, iRight = right - 1;;) {
			while (arr[++iLeft] < medianNumber) ;
			while (arr[--iRight] > medianNumber) ;
			if (iLeft >= iRight) break;
			swap(arr, iLeft, iRight);
		}
		swap(arr, iLeft, right - 1);
		return iLeft;
	}

	// Quicksort helper method
	private static int median3sort(int[] arr, int left, int right) {
		int medianIndex = (left + right) / 2;
		if (arr[left] > arr[medianIndex]) swap(arr, left, medianIndex);
		if (arr[medianIndex] > arr[right]) {
			swap(arr, medianIndex, right);
			if (arr[left] > arr[medianIndex]) swap(arr, left, medianIndex);
		}
		return medianIndex;
	}

	// Quicksort method
	public static void quicksort(int[] arr, int left, int right) {
		if (right - left > 2) {
			int splitpos = split(arr, left, right);
			quicksort(arr, left, splitpos - 1);
			quicksort(arr, splitpos + 1, right);
		} else {
			median3sort(arr, left, right);
		}
	}

	// Generate an array of given length with random numbers between 0 and length
	private static int[] getRandomArr(int length) {
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = (int) (Math.random() * length);
		}
		return arr;
	}

	// Generate an array of given length with random numbers between 0 and length, where every even index equals half of length
	private static int[] getHalfRandomArr(int length) {
		int[] arr = new int[length];
		int duplicate1 = length / 2;
		int duplicate2 = length / 3;
		for (int i = 0; i < length; i++) {
			if (i % 2 == 0) {
				arr[i] = (int) (Math.random() * length);
			} else if (i % 3 == 0) {
				arr[i] = duplicate1;
			} else {
				arr[i] = duplicate2;
			}
		}
		return arr;
	}

	// Generate an array of given length with sorted numbers from 0 to length
	private static int[] getSortedArr(int length) {
		int[] arr = new int[length];
		for (int i = 0; i < length; i++) {
			arr[i] = i;
		}
		return arr;
	}

	// Get the sum of all numbers in an array
	private static long getSumOfArr(int[] arr) {
		return Arrays.stream(arr).count();
	}

	// Check that an array is sorted
	private static boolean isSorted(int[] a) {
		for (int i = 1; i <= a.length - 1; i++)
			if (a[i] < a[i-1]) return false;
		return true;
	}

	// Test how long each function uses to sort a given array
	private static void testTime(int[] arr, String title) {
		int[] singleArr = arr.clone();
		int[] dualArr = arr.clone();
		int[] javaArr = arr.clone();

		// Take time of Single pivot Quicksort
		long sumStartSingle = getSumOfArr(singleArr);
		long startTimeSingle = System.nanoTime();
		quicksort(singleArr, 0, singleArr.length - 1);
		long timeSingle = System.nanoTime() - startTimeSingle;
		long sumEndSingle = getSumOfArr(singleArr);

		// Take time of Dual pivot Quicksort
		long sumStartDual = getSumOfArr(dualArr);
		long startTimeDual = System.nanoTime();
		dualPivotQuickSort(dualArr, 0, dualArr.length - 1);
		long timeDual = System.nanoTime() - startTimeDual;
		long sumEndDual = getSumOfArr(dualArr);

		// Take time of Arrays.sort()
		long sumStartJava = getSumOfArr(javaArr);
		long startTimeJava = System.nanoTime();
		Arrays.sort(javaArr);
		long timeJava = System.nanoTime() - startTimeJava;
		long sumEndJava = getSumOfArr(javaArr);

		System.out.println("--- " + title + " ---");
		System.out.format("%-15s%-9s%-7s%-7s%n", "Sort algorthm:", "Time:", "Equal:", "Sorted:");
		System.out.format(TIME_COLUMNS_FORMAT, "Quicksort", timeSingle / 1000000 + " ms", (sumStartSingle == sumEndSingle), isSorted(singleArr));
		System.out.format(TIME_COLUMNS_FORMAT, "Dual pivot", timeDual / 1000000 + " ms", (sumStartDual == sumEndDual), isSorted(dualArr));
		System.out.format(TIME_COLUMNS_FORMAT, "Java sort", timeJava / 1000000 + " ms", (sumStartJava == sumEndJava), isSorted(javaArr));
		System.out.println("");
	}

	public static void main(String[] args) {
		int length = 10000000;
		System.out.println("Length of arrays: " + length + "\n");
		testTime(getRandomArr(length), "Random array");
		testTime(getHalfRandomArr(length), "Half random array");
		testTime(getSortedArr(length), "Sorted array");
	}
}
