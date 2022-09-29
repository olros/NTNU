import scipy.sparse as sp
import numpy as np


def CGF(A, b, x0=None, tol=None):

    # Create an initial guess if needed
    if x0 is None:
        x0 = np.zeros(len(A[0]))

    if tol is None:
        tol = 0

    (n, n) = A.shape
    x = []
    r = []
    d = []
    alpha = []
    beta = []
    x.append(x0)
    r.append(b - A @ x[0])
    d.append(r[0])
    for k in range(n):
        err = np.linalg.norm(r[-1])
        if err < tol:
            break
        alpha.append((r[k].dot(r[k])) / d[k].dot(A.dot(r[k])))
        x.append(x[k] + alpha[k] * d[k])
        r.append(r[k] - alpha[k] * A.dot(d[k]))
        beta.append((r[k + 1].dot(r[k + 1])) / (r[k].dot(r[k])))
        d.append(r[k + 1] + beta[k] * d[k])
    return x[-1], k, r[-1]


# 1 a)

A = np.array([[1, 0], [0, 2]])
b = np.array([2, 4])
x0 = np.array([1, 0])
tol = 2.0 ** (-52)

print(CGF(A, b, x0, tol))

# 1 b)
A = np.array([[1, 2], [2, 5]])
b = np.array([1, 1])
x0 = np.array([0, 0])
tol = 2.0 ** (-52)

print(CGF(A, b, x0, tol))
