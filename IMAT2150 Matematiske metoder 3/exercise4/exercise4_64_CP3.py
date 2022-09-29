import numpy as np


from math import e
from methods import runge_kutta


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


# Steps 0.1
print("h = 0.1\n\n")
result = runge_kutta(f, interval, y0, steps)

full_results = np.array([result[1], result[0], np.zeros(steps + 1)]).transpose()

for i in range(steps + 1):
    full_results[i, 2] = abs(full_results[i, 0] - f_exact(full_results[i, 1]))

print("[t, w, error]")
print(full_results)

# Steps 0.05
print("h = 0.05\n\n")
steps = 20
result = runge_kutta(f, interval, y0, steps)

full_results = np.array([result[1], result[0], np.zeros(steps + 1)]).transpose()

for i in range(steps + 1):
    full_results[i, 2] = abs(full_results[i, 0] - f_exact(full_results[i, 1]))

print("[t, w, error]")
print(full_results)

# Steps = 0.025
print("h = 0.025")
steps = 40
result = runge_kutta(f, interval, y0, steps)

full_results = np.array([result[1], result[0], np.zeros(steps + 1)]).transpose()

for i in range(steps + 1):
    full_results[i, 2] = abs(full_results[i, 0] - f_exact(full_results[i, 1]))

print("[t, w, error]")
print(full_results)