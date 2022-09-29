from scipy import sparse as sp
import numpy as np
import matplotlib.pyplot as plt


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


n = 10000
e = np.ones(n)
D = sp.spdiags(3 * e, 0, n, n)
U = sp.spdiags(-1 * e, 1, n, n)
L = sp.spdiags(-1 * e, -1, n, n)
A = D + U + L
A = A.toarray()

for i in range(n):
    if A[n - i - 1][i] == 0:
        A[n - i - 1][i] = 0.5

b = np.zeros(n)
for i in range(n):
    if i == 0 or i == n - 1:
        b[i] = 2.5
    elif i == (n / 2) or i == (n / 2) - 1:
        b[i] = 1.0
    else:
        b[i] = 1.5

tol = 2.0 ** (-52)
x0 = np.zeros(n)

result = CGF(A, b, x0, tol)

print("values: ", result[0])
print("k - iterations:", result[1])
print("residuals:\n", result[2])
