import numpy as np
import sys


def naive_gauss_elimination(A):
    n = len(A)
    L = np.zeros((n, n))
    for i in range(n):
        if A[i][i] == 0.0:
            sys.exit("Divide by zero detected!")
        L[i][i] = 1

        for j in range(i + 1, n):
            ratio = A[j][i] / A[i][i]
            L[j][i] = ratio

            for k in range(n):
                A[j][k] = A[j][k] - ratio * A[i][k]

    U = A
    print(f"L:\n{L}\nU:\n{U}")
    return [L, U]


if __name__ == "__main__":
    task_2a_matrix = np.array([[3, 1, 2], [6, 3, 4], [3, 1, 5]])
    task_2b_matrix = np.array([[4, 2, 0], [4, 4, 2], [2, 2, 3]])
    task_2c_matrix = np.array(
        [[1, -1, 1, 2], [0, 2, 1, 0], [1, 3, 4, 4], [0, 2, 1, -1]]
    )

    print("2a)")
    naive_gauss_elimination(task_2a_matrix)
    print("\n2b)")
    naive_gauss_elimination(task_2b_matrix)
    print("\n2c)")
    naive_gauss_elimination(task_2c_matrix)
