from typing import Any, Tuple

import numpy as np
import torch
import torch.nn as nn

from src.constants import MODEL_SAVE_NAME, TARGET_MODEL_SAVE_NAME


class DQN(nn.Module):
    def __init__(self, input_shape, n_actions):
        super(DQN, self).__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(input_shape[0], 32, kernel_size=8, stride=4),
            nn.ReLU(),
            nn.Conv2d(32, 64, kernel_size=4, stride=2),
            nn.ReLU(),
            nn.Conv2d(64, 64, kernel_size=3, stride=1),
            nn.ReLU(),
        )

        conv_out_size = self._get_conv_out(input_shape)
        self.fc = nn.Sequential(
            nn.Linear(conv_out_size, 512), nn.ReLU(), nn.Linear(512, n_actions)
        )

    def _get_conv_out(self, shape: Tuple):
        """Function to get the output shape of the cnn

        Args:
            shape (Tuple): the input shape to the cnn

        Returns:
            int: the output size of the cnn
        """
        o = self.conv(torch.zeros(1, *shape))
        return int(np.prod(o.size()))

    def forward(self, x: Any):
        """Function to use the network on a given state in the shape of (1, *input_shape)

        Args:
            x (Any): given state to calculate from

        Returns:
            Tuple: the given Q-table for the given state
        """
        conv_out = self.conv(x).view(x.size()[0], -1)
        return self.fc(conv_out)

    def save(self, target: bool = False):
        """Function to save the model to file

        Args:
            target (bool, optional): boolean to tell if the model is a target network or not. Defaults to False.
        """
        name = TARGET_MODEL_SAVE_NAME if target else MODEL_SAVE_NAME
        torch.save(self.state_dict(), name)

    def load(self, device: str, target: bool = False):
        """Function to load the a model to a given device cpu or gpu

        Args:
            device (str): the given device
            target (bool, optional): boolean to tell if the model is a target network or not . Defaults to False.
        """
        name = TARGET_MODEL_SAVE_NAME if target else MODEL_SAVE_NAME
        self.load_state_dict(torch.load(name, map_location=torch.device(device)))
