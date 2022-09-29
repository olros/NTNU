from numpy import *
from pylab import plot, show
from methods import euler_multi as euler


def ydotfunc(t,y):
	return array( [ y[1] + y[0], -y[0]+y[1]] )


y1_exact = 1.46869394
y2_exact = -2.287355287

y1, t1 =euler([0,1], [1.,0], 10, ydotfunc )
print(y1[-1])
print("y1 approximate: ", y1[-1, 0])
print("y1 global error: ", abs(y1[-1,0] - y1_exact))
print("\n")
print("y2 approximate: ", y1[-1,1])
print("y1 global error: ", abs(y1[-1, 1] - y2_exact))



y2, t2 =euler([0,1], [1.,0], 100, ydotfunc)
print("y1 approximate: ", y2[-1, 0])
print("y1 global error: ", abs(y2[-1, 0] - y1_exact))
print("\n")
print("y2 approximate: ", y2[-1, 1])
print("y1 global error: ", abs(y2[-1, 1] - y2_exact))