#%%
import torch
# import matplotlib.pyplot as plt
import pandas as pd


import numpy as np
import matplotlib
import matplotlib.pyplot as plt

matplotlib.rcParams.update({'font.size': 11})

class LinearRegressionModel:
    def __init__(self):
        self.W = torch.rand((2, 1), requires_grad=True)
        self.b = torch.rand((1, 1), requires_grad=True)

    # predictor
    def f(self, x):
        return x @ self.W + self.b

    # Uses Mean Squared Error
    def loss(self, x, y):
        return torch.mean(torch.square(self.f(x) - y))


model = LinearRegressionModel()

data = pd.read_csv('age_length_weight.csv', names=["Age", "Length", "Weight"])

x = []
length = data.Length.to_list()
weight = data.Weight.to_list()
age = data.Age.to_list()
for i in range(len(length)):
    x.append([length[i], weight[i]])

x_train = torch.tensor([x]).reshape(-1, 2)
y_train = torch.tensor([age]).reshape(-1, 1)

# Optimize: adjust W and b to minimize loss using stochastic gradient descent
optimizer = torch.optim.SGD([model.W, model.b], 0.000115)
for epoch in range(100000):
    model.loss(x_train, y_train).backward()  # Compute loss gradients
    optimizer.step()  # Perform optimization by adjusting W and b,

    optimizer.zero_grad()  # Clear gradients for next step

# Print model variables and loss
print("W = %s, b = %s, loss = %s" % (model.W, model.b, model.loss(x_train, y_train)))

fig = plt.figure('Linear regression: 3D')

plot1 = fig.add_subplot(111, projection='3d')

plot1.scatter(weight, age, length)
plot1.scatter(weight, model.f(x_train).detach().tolist(), length)

plt.show()


# %%
