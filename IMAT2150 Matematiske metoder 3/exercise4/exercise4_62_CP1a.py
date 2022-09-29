import numpy as np
from math import e
from methods import trapezoid 

        
# 1a
# Defining the function to use
def f(t, y):
    return t

interval = np.array([0, 1])
y0 = 1
steps = 10

#  1a Exact function
def f_exact(t):
    return 0.5*t**2 + 1


# Results
result = trapezoid(f, interval, y0, steps)

full_results = np.array([result[0], result[1], np.zeros(steps + 1)]).transpose()

for i in range(steps + 1):
    full_results[i, 2] = abs(full_results[i, 1] - f_exact(full_results[i, 0]))

print("[t, w, error]")
print(full_results)