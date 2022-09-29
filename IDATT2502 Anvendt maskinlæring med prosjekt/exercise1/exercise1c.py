#%%
import torch
import matplotlib.pyplot as plt
import pandas as pd

data = pd.read_csv('day_head.csv', names=["Day", "Head"])

x_train = torch.tensor([data.Day.to_list()]).reshape(-1, 1)
y_train = torch.tensor([data.Head.to_list()]).reshape(-1, 1)

class NonLinearRegressionModel:
    def __init__(self):
        # Model variables
        self.W = torch.tensor([[0.0]], requires_grad=True)  # requires_grad enables calculation of gradients
        self.b = torch.tensor([[0.0]], requires_grad=True)

    # Predictor
    def f(self, x):
        return 20 * torch.sigmoid((x @ self.W + self.b)) + 31

    # Uses Mean Squared Error
    def loss(self, x, y):
        return torch.mean(torch.square(self.f(x) - y))  # Can also use torch.nn.functional.mse_loss(self.f(x), y) to possibly increase numberical stability

# %%


model = NonLinearRegressionModel()

# Optimize: adjust W and b to minimize loss using stochastic gradient descent
optimizer = torch.optim.SGD([model.W, model.b], 0.00015)
for epoch in range(100000):
    model.loss(x_train, y_train).backward()  # Compute loss gradients
    optimizer.step()  # Perform optimization by adjusting W and b,

    optimizer.zero_grad()  # Clear gradients for next step
# %%
# Print model variables and loss
print("W = %s, b = %s, loss = %s" % (model.W, model.b, model.loss(x_train, y_train)))

# Visualize result
plt.plot(x_train, y_train, 'o', label='$(x^{(i)},y^{(i)})$')
plt.xlabel('x')
plt.ylabel('y')
x = torch.arange(torch.min(x_train), torch.max(x_train), 1.0).reshape(-1, 1)
plt.plot(x, model.f(x).detach().cpu(), label='$\\hat y = f(x) = xW+b$')
plt.legend()
plt.show()

# print(model.f(torch.tensor([[50.0], [80.0], [100.0]])))

# %%
