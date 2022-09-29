from numpy import *


def fixedPointIteration(func, start, iterations):
    x = start
    for i in range(0, iterations):
        y = x
        x = func(x)
        print(f"------------ was: {y}, f(x): {x} ------------")
    return x


def f(x):
    return (x ** 2) - x + 2


print(f"Ans: {fixedPointIteration(f, 1, 4)}")


#
# def fix_iter(g,s,n):
#     x = s
#     for k in range(1,n):
#         x = g(x)
#     return x

# ## a small experiment with the function from the lecture
# # definition of the function
# def fix_fun(x):
#     return x**2 - 4*x + 2

# # you may freely change starting point and number of iterations
# start_point = 0
# n_iter = 500

# # run the iteration (and display the result)
# print(fix_iter(fix_fun,start_point,n_iter))
