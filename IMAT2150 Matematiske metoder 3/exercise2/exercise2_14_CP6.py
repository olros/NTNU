import numpy as np

exact_volume = 60  # (cm^3)
cone_height = 10  # (cm)
digits_accuracy = 4

hemispherical_volume = lambda radius: (2 / 3) * np.pi * radius ** 3

cone_volume = lambda radius: (1 / 3) * np.pi * radius ** 2 * cone_height

f = lambda radius: cone_volume(radius) + hemispherical_volume(radius) - 60


hemispherical_volume_d = lambda radius: 2 * np.pi * radius ** 2

cone_volume_d = lambda radius: (2 / 3) * np.pi * radius * cone_height

f_d = lambda radius: cone_volume_d(radius) + hemispherical_volume_d(radius)

x_i = 1
print(f"x_0: {x_i}")
for i in range(1, 20):
    x_prev = x_i
    x_i = x_i - (f(x_i) / f_d(x_i))
    print(f"x_{i}: {x_i}")
    if np.abs(x_prev - x_i) < 0.5 * 10 ** (-digits_accuracy):
        print(f"Radius found: {x_i} with {digits_accuracy} digits accuracy")
        break
