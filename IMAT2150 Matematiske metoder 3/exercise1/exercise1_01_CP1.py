from numpy import *


def nest(c, x, b=[]):
    d = len(c) - 1
    if b == []:
        b = zeros(d)
    y = c[d]
    for i in range(d - 1, -1, -1):
        y *= x - b[i]
        y += c[i]
    return y


def equivalent(d, x):
    return ((x ** d) - 1) / (x - 1)


ans = nest(ones(51), 1.00001)
eqv = equivalent(51, 1.00001)

print(f"Ans: {ans}")
print(f"Equivalent: {eqv}")
print(f"Diff: {absolute(eqv - ans)}")
