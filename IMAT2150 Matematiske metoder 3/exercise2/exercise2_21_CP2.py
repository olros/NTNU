from exercise2_21_CP1 import naive_gauss_elimination
import numpy as np


def hilbert(n):
    x = np.arange(1, n + 1) + np.arange(0, n)[:, np.newaxis]
    h = 1.0 / x
    return np.append(h, np.ones((n, 1)), axis=1)


task_2a_matrix = [[1, -2, -1, -2], [4, 1, -2, 1], [-2, 1, -1, -3]]

print("a)")
naive_gauss_elimination(hilbert(2))
print("\nb)")
naive_gauss_elimination(hilbert(5))
print("\nc)")
naive_gauss_elimination(hilbert(10))
