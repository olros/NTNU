import numpy as np

from typing import Callable

def eulerstep(t,w,h, ydot):
	return w + h*ydot(t,w)

def euler(interval: np.ndarray, y0: int,n: int, ydot: Callable) -> tuple:
    """euler method for one varible diff equations

    Args:
        interval (np.ndarray): the interval to evaluate
        y0 (int): the intial y value
        n (int): how many steps we want
        ydot (Callable): f(t, y) function

    Returns:
        tuple: the w, t values of the method
    """    
    h = float(interval[1]-interval[0])/n
    t = [i*h for i in range(n+1)]
    w = [y0]
    for i in range(n):
        w.append(eulerstep(t[i],w[i],h, ydot) )
    return w, t

def trapezoid(func: Callable, inter: np.ndarray, y0: np.ndarray, n:int) -> tuple:
    """the trapezoid method for evaluation of diff equations

    Args:
        func (Callable): f(t, y) function
        inter (np.ndarray): interval as an array with 2 elements for example [0, 1]
        y0 (np.ndarray): y(0) = y0, the initial value
        n (int): how many steps we want

    Returns:
        tuple: the t and w values of the method
    """        
    t = np.zeros(n + 1)
    w = np.zeros(n + 1)

    t[0] = inter[0]
    w[0] = y0
    h = (inter[1] - inter[0])/n

    for i in range(1, n + 1):
        t[i] = t[i - 1] + h
        w[i] = w[i - 1] + (h/2)*(func(t[i - 1], w[i - 1]) + func(t[i - 1] + h, w[i-1] + (h * func(t[i-1], w[i - 1]))))
    
    return t, w

def euler_multi(interval: np.ndarray,y0: np.ndarray,n: int, ydot: Callable) -> tuple:
    """euler method for multi functional diff equations

    Args:
        interval (np.ndarray): 
        y0 (np.ndarray):  interval as an array with 2 elements for example [0, 1]
        n (int):  how many steps we want
        ydot (Callable):  the function to evaluate 

    Returns:
        tuple: the w and t values of the method
    """    
    h = float(interval[1]-interval[0])/n
    t = [i*h for i in range(n+1)]
    w = np.empty((n+1,2)); w[0,:] = y0
    for i in range(n):
        w[i+1,:] = eulerstep(t[i],w[i,:],h, ydot) 
    return w, t


def midpoint(func: Callable, inter: np.ndarray, y0: np.ndarray, n: int) ->tuple:
    """midpoint method for diff equations

    Args:
        func (Callable): f(t, y) function
        inter (np.ndarray): the interval to evaluate
        y0 (np.ndarray): y(0) = y0, the initial value
        n (int): how many steps we want

    Returns:
        tuple: the w and t values of the method
    """    
    t = np.zeros(n + 1)
    w = np.zeros(n + 1)

    t[0] = inter[0]
    w[0] = y0
    h = (inter[1] - inter[0])/n

    for i in range(1, n + 1):
        t[i] = t[i - 1] + h
        w[i] = w[i - 1] + (h*func(t[i - 1] + (h/2), w[i - 1] + ((h/2)*func(t[i-1], w[i-1]))))
    
    return w, t


def runge_kutta(func: Callable, inter: np.ndarray, y0: np.ndarray, n: int) ->tuple:
    """Runge kutta method for differential equations

    Args:
        func (Callable): f(t, y) function
        inter (np.ndarray): interval as an array with 2 elements for example [0, 1]
        y0 (np.ndarray): y(0) = y0, the initial value
        n (int):  how many steps we want

    Returns:
        tuple:  the w and t values after method is done
    """    
    t = np.zeros(n + 1)
    w = np.zeros(n + 1)

    t[0] = inter[0]
    w[0] = y0
    h = (inter[1] - inter[0])/n

    for i in range(1, n + 1):
        t[i] = t[i - 1] + h
        s_1 = func(t[i - 1], w[i -1 ])
        s_2 = func(t[i - 1] + (h/2), w[i - 1] + ((h*s_1) /2))
        s_3 = func(t[i - 1] + (h/2), w[i - 1] + ((h*s_2) /2))
        s_4 = func(t[i - 1] + h, w[i - 1] + (h*s_3))

        w[i] = w[i - 1] + ((h/6)*(s_1 + (2*s_2) + (2*s_3) + s_4))
    return w, t