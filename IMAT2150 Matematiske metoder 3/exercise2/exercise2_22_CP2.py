import numpy as np
import sys
from exercise2_22_CP1 import naive_gauss_elimination


def solve_with_LU(A, b):
    [L, U] = naive_gauss_elimination(A)

    n = len(L)

    c = np.zeros(n)
    c[0] = b[0] / L[0, 0]

    for i in range(1, n):
        c[i] = (b[i] - np.dot(L[i, :i], c[:i])) / L[i, i]

    x = np.zeros(n)

    x[-1] = c[-1] / U[-1, -1]

    for i in range(n - 2, -1, -1):
        x[i] = (c[i] - np.dot(U[i, i:], x[i:])) / U[i, i]

    print("The solution is: ")
    for i in range(n):
        print(f"X{i + 1} = {x[i]}")
    return x


if __name__ == "__main__":
    task_4a_matrix_A = np.array([[3, 1, 2], [6, 3, 4], [3, 1, 5]])
    task_4a_matrix_b = np.array([0, 1, 3])
    task_4b_matrix_A = np.array([[4, 2, 0], [4, 4, 2], [2, 2, 3]])
    task_4b_matrix_b = np.array([2, 4, 6])

    print("2a)")
    solve_with_LU(task_4a_matrix_A, task_4a_matrix_b)
    print("\n2b)")
    solve_with_LU(task_4b_matrix_A, task_4b_matrix_b)
