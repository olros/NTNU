import numpy as np


from math import e
from methods import midpoint



        


# 1a
# Defining the function to use
def f(t, y):
    return t*t*y

interval = np.array([0, 1])
y0 = 1
steps = 10

#  1a Exact function
def f_exact(t):
    return e**((1/3)*t**3)


# Results
result = midpoint(f, interval, y0, steps)

full_results = np.array([result[1], result[0], np.zeros(steps + 1)]).transpose()

for i in range(steps + 1):
    full_results[i, 2] = abs(full_results[i, 0] - f_exact(full_results[i, 1]))

print("[t, w, error]")
print(full_results)