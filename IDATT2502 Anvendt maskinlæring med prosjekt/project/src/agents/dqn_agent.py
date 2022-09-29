import pickle

import numpy as np
import torch
from torch import nn

from src.constants import (
    BATCH_SIZE,
    ENDING_POSISTION_PICKLE,
    EPSILON,
    EPSILON_DECAY_RATE,
    GAMMA,
    LEARNING_RATE,
    MEMORY_SIZE,
    MIN_EPSILON,
    NUM_IN_QUEUE_PICKLE,
    OPTIMIZER_EPSILON,
    TOTAL_REWARDS_PICKLE,
)
from src.models.dqn import DQN
from src.replay_buffer import ReplyBuffer


class DQNAgent:
    def __init__(
        self,
        env,
        input_shape,
        action_shape,
        epsilon_decay_rate=EPSILON_DECAY_RATE,
        min_epsilon=MIN_EPSILON,
        epsilon=EPSILON,
        batch_size=BATCH_SIZE,
        gamma=GAMMA,
        memory_size=MEMORY_SIZE,
    ):
        self.state_space = input_shape
        self.env = env
        self.action_space = action_shape
        self.memory_size = memory_size
        self.memory = ReplyBuffer(input_shape, memory_size)
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.min_epsilon = min_epsilon
        self.epsilon_decay_rate = epsilon_decay_rate
        self.epsilon = epsilon
        self.batch_size = batch_size
        self.gamma = gamma
        self.model = DQN(input_shape, action_shape).to(self.device)
        self.step = 0
        self.ending_position = 0
        self.num_in_queue = 0
        self.loss_func = nn.SmoothL1Loss().to(self.device)
        self.optimizer = torch.optim.Adam(
            self.model.parameters(), lr=LEARNING_RATE, eps=OPTIMIZER_EPSILON
        )

    def load(self):
        """Function to load a DQN model from the given name in constants
        """
        self.model.load(self.device)
        self.optimizer = torch.optim.Adam(self.model.parameters(), lr=LEARNING_RATE)
        self.memory.load()
        self.memory_size = MEMORY_SIZE
        with open(ENDING_POSISTION_PICKLE, "rb") as f:
            self.ending_position = pickle.load(f)
        with open(NUM_IN_QUEUE_PICKLE, "rb") as f:
            self.num_in_queue = pickle.load(f)

    def save(self, total_rewards: int):
        """Function to save the model and target_model, along with the replay buffer

        Args:
            total_rewards (int): the total reward at the time of saving
        """
        self.model.save()
        self.memory.save()
        with open(ENDING_POSISTION_PICKLE, "wb") as f:
            pickle.dump(self.ending_position, f)
        with open(NUM_IN_QUEUE_PICKLE, "wb") as f:
            pickle.dump(self.num_in_queue, f)
        with open(TOTAL_REWARDS_PICKLE, "wb") as f:
            pickle.dump(total_rewards, f)

    def update_epsilon(self):
        """Function to decay epsilon by the epsilon decay rate
        """
        self.epsilon *= self.epsilon_decay_rate
        self.epsilon = max(self.min_epsilon, self.epsilon)

    def act(self, state: np.ndarray) -> int:
        """Function to pick an action based on epsilon (exploration rate)

        Args:
            state (np.ndarray): the given state to preform an action based on

        Returns:
            int: the optimal action given the state, or a random state if exploration was picked
        """
        if np.random.rand() < self.epsilon:
            action = np.random.randint(self.action_space)
        else:
            action_values = self.model(
                torch.tensor(state, dtype=torch.float32, device=self.device)
            )
            action = torch.argmax(action_values, dim=1).item()
        self.update_epsilon()
        self.step += 1
        return action

    def play(self, state: np.ndarray) -> int:
        """Function to play the environment based on a trained model

        Args:
            state (np.ndarray): given state to base the action on

        Returns:
            int: best action given the current state
        """
        return (
            torch.argmax(self.model(state.to(self.device)))
            .unsqueeze(0)
            .unsqueeze(0)
            .cpu()
        )

    def remember(
        self,
        state: torch.Tensor,
        action: torch.Tensor,
        reward: int,
        next_state: torch.Tensor,
        done: bool,
    ):
        """Function to remember a result from taking an action in the environment

        Args:
            state (torch.Tensor): state that the action was calculated from
            action (torch.Tensor): the action that was preformed
            reward (int): the given reward that the action gave
            next_state (torch.Tensor): the next state given the action
            done (bool): boolean that says if the action lead to round being over
        """
        self.ending_position = (self.ending_position + 1) % self.memory_size
        self.num_in_queue = min(self.num_in_queue + 1, self.memory_size)
        self.memory.append(
            state, action, reward, next_state, done, self.ending_position
        )

    def update_q_values(
        self, reward: torch.Tensor, done: torch.Tensor, next_state: torch.Tensor,
    ) -> torch.Tensor:
        """Function to update the q-values of a given batch of memory based on the bellman equation

        Args:
            reward (torch.Tensor): rewards for the given batch
            done (torch.Tensor): array that says if the given action ended the round or not
            next_state (torch.Tensor): the next state to optimize the q values based on

        Returns:
            torch.Tensor: the updated q values
        """
        return reward + torch.mul(
            (self.gamma * self.model(next_state).max(1).values.unsqueeze(1)), 1 - done,
        )

    def replay(self):
        """Function to train the model based on a random batch of experiences from the replay buffer
        """

        if self.batch_size > self.num_in_queue:
            return

        state, action, reward, next_sates, done = self.memory.recall(
            self.num_in_queue, self.batch_size, self.device
        )
        target = self.update_q_values(reward, done, next_sates)
        current = self.model(state).gather(1, action.long())
        loss = self.loss_func(current, target)
        self.optimizer.zero_grad()
        loss.backward()
        self.optimizer.step()
