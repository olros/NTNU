from numpy import *


def bisect(func, start, end):
    min = start
    mid = (end - start) / 2
    max = end
    for i in range(0, 25):
        newMin = func(min)
        newMid = func(mid)
        newMax = func(max)
        print(f"------------ Min: {newMin}, mid: {newMid}, max: {newMax} ------------")
        if newMid == 0:
            return newMid
        elif (newMin < 0 and newMid < 0) or (newMin > 0 and newMid > 0):
            min = mid
            mid = ((max - min) / 2) + min
        elif (newMax > 0 and newMid > 0) or (newMax < 0 and newMid < 0):
            max = mid
            mid = ((max - min) / 2) + min
        print(f"Min: {min}, mid: {mid}, max: {max}")
    return mid


def f(x):
    return (cos(x) ** 2) + 6 - x


print(f"Ans: {bisect(f, 0, 8)}")
