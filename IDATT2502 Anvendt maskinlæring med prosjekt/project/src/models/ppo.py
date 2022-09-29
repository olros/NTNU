from typing import Any

import torch
import torch.nn as nn
from torch.distributions import Categorical

from src.constants import PPO_MODEL_SAVE_NAME


class PPO(nn.Module):
    def __init__(self, input_shape, n_actions):
        super(PPO, self).__init__()
        self.actor = nn.Sequential(
            nn.Conv2d(
                in_channels=input_shape[0], out_channels=32, kernel_size=8, stride=4
            ),
            nn.ReLU(),
            nn.Conv2d(in_channels=32, out_channels=64, kernel_size=4, stride=2),
            nn.ReLU(),
            nn.Conv2d(in_channels=64, out_channels=64, kernel_size=3, stride=1),
            nn.ReLU(),
            nn.Flatten(),
            nn.Linear(3136, 512),  # comes from output from Conv2d layer 3 64*7*7
            nn.ReLU(),
            nn.Linear(512, n_actions),
        )
        self.critic = nn.Sequential(
            nn.Conv2d(
                in_channels=input_shape[0], out_channels=32, kernel_size=8, stride=4
            ),
            nn.ReLU(),
            nn.Conv2d(in_channels=32, out_channels=64, kernel_size=4, stride=2),
            nn.ReLU(),
            nn.Conv2d(in_channels=64, out_channels=64, kernel_size=3, stride=1),
            nn.ReLU(),
            nn.Flatten(),
            nn.Linear(3136, 512),  # comes from output from Conv2d layer 3 64*7*7
            nn.ReLU(),
            nn.Linear(512, 1),
        )

    def forward(self, x: Any):
        """Function to use the network on a given state in the shape of (1, *input_shape)

        Args:
            x (Any): given state to calculate from

        Returns:
            Tuple: the given action to preform, and the given q-tabell for the given state
        """
        return (
            Categorical(logits=self.actor(x)),
            self.critic(x).reshape(-1),
        )

    def save(self):
        """Function to save the model to file
        """
        torch.save(self.state_dict(), PPO_MODEL_SAVE_NAME)

    def load(self, device: str):
        """Function to load a model to a given device (cpu or gpu)

        Args:
            device (str): the given device
        """
        self.load_state_dict(
            torch.load(PPO_MODEL_SAVE_NAME, map_location=torch.device(device))
        )
