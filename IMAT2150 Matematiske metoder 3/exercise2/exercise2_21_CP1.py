import numpy as np
import sys


def naive_gauss_elimination(A):
    n = len(A)
    x = np.zeros(n)
    for i in range(n):
        if A[i][i] == 0.0:
            sys.exit("Divide by zero detected!")

        for j in range(i + 1, n):
            ratio = A[j][i] / A[i][i]

            for k in range(n + 1):
                A[j][k] = A[j][k] - ratio * A[i][k]

    x[n - 1] = A[n - 1][n] / A[n - 1][n - 1]

    for i in range(n - 2, -1, -1):
        x[i] = A[i][n]

        for j in range(i + 1, n):
            x[i] = x[i] - A[i][j] * x[j]

        x[i] = x[i] / A[i][i]

    print("The solution is: ")
    for i in range(n):
        print(f"X{i + 1} = {x[i]}")


if __name__ == "__main__":
    task_2a_matrix = [[1, -2, -1, -2], [4, 1, -2, 1], [-2, 1, -1, -3]]
    task_2b_matrix = [[1, 2, -1, 2], [0, 3, 1, 4], [2, -1, 1, 2]]
    task_2c_matrix = [[2, 1, -4, -7], [1, -1, 1, -2], [-1, 3, -2, 6]]

    print("2a)")
    naive_gauss_elimination(task_2a_matrix)
    print("\n2b)")
    naive_gauss_elimination(task_2b_matrix)
    print("\n2c)")
    naive_gauss_elimination(task_2c_matrix)
