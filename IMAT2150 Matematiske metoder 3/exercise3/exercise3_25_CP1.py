from scipy import sparse as sp
import numpy as np


def jacobi(A, b, k, x=None):
    d = np.diagonal(A)
    r = A - np.diagflat(d)

    # Create an initial guess if needed
    if x is None:
        x = np.zeros(len(A[0]))

    counter = 0
    for i in range(k):
        print(x)
        if x[0] > (1 - 0.000001):
            break
        x = (b - (r @ x)) / d
        counter += 1
    print(
        "iterations needed to find a correct answer with 6 correct decimal places",
        counter,
    )
    return x


n = 100
e = np.ones(n)
D = sp.spdiags(3 * e, 0, n, n)
U = sp.spdiags(-1 * e, 1, n, n)
L = sp.spdiags(-1 * e, -1, n, n)
A = D + U + L

b = np.zeros(n)
b[0] = 2
b[n - 1] = 2
for i in range(n - 2):
    b[i + 1] = 1

print(jacobi(A.toarray(), b, 100))
