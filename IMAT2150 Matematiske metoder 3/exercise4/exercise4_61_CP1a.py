from pylab import plot, show
from numpy import *
from math import *

from methods import euler

def ydota(t,y):
	return t

def ydotb(t,y):
	return t**2*y

def a_func(t):
    return 0.5*t**2 + 1

def b_func(t):
    return exp((t**3)/3)
#a
y, t = euler([0,1], 1, 10, ydota) 

def print_solution(y, t, exact):
    for i, value in enumerate(y):
        print(f"step nr {i}")
        print(f"euler approximations\n {value}")
        print(f"t values {t[i]}")
        print(f"error {t[i]} = {abs(exact(t[i])-value)}\n\n")

print("a")
print_solution(y,t, a_func)

#b
y,t =  euler([0,1], 1, 10, ydotb) 
print("b")
print_solution(y,t,b_func)

