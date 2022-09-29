import random
from typing import Tuple

import torch

from src.constants import (
    ACTION_SAVE_NAME,
    DONE_SAVE_NAME,
    NEXT_STATE_SAVE_NAME,
    REWARD_SAVE_NAME,
    STATE_SAVE_NAME,
)


class ReplyBuffer:
    def __init__(self, state_space, memory_size):
        self.state_space = state_space
        self.sate_mem = torch.zeros(memory_size, *self.state_space)
        self.action_mem = torch.zeros(memory_size, 1)
        self.reward_mem = torch.zeros(memory_size, 1)
        self.next_state_mem = torch.zeros(memory_size, *self.state_space)
        self.done_mem = torch.zeros(memory_size, 1)

    def append(
        self,
        state: torch.Tensor,
        action: int,
        reward: float,
        next_state: torch.Tensor,
        done: bool,
        end_pos: int,
    ):
        """Function to remember a given result from a action in the environment

        Args:
            state (torch.Tensor): the given state that lead to the action
            action (int): the action preformed
            reward (float): the reward the action resulted in
            next_state (torch.Tensor): the next state given the action
            done (torch.Tensor): boolean that says if the given aciton ended the round or not
            end_pos (int): the place in the memory to append the result
        """
        self.sate_mem[end_pos] = state.float()
        self.action_mem[end_pos] = action.float()
        self.reward_mem[end_pos] = reward.float()
        self.next_state_mem[end_pos] = next_state.float()
        self.done_mem[end_pos] = done.float()

    def recall(self, num_in_queue: int, memory_sample_size: int, device: str) -> Tuple:
        """Function to recall a given number of result

        Args:
            num_in_queue (int): where to remember from
            memory_sample_size (int): the batch size to pick out of the replay buffer
            device (str): the device to load the tensors into

        Returns:
            Tuple: tensors with the recalled memory results
        """

        idx = random.choices(range(num_in_queue), k=memory_sample_size)

        state = self.sate_mem[idx].to(device)
        action = self.action_mem[idx].to(device)
        reward = self.reward_mem[idx].to(device)
        next_state = self.next_state_mem[idx].to(device)
        done = self.done_mem[idx].to(device)

        return state, action, reward, next_state, done

    def save(self):
        """Function to save the current replay buffer
        """
        torch.save(self.sate_mem, STATE_SAVE_NAME)
        torch.save(self.action_mem, ACTION_SAVE_NAME)
        torch.save(self.reward_mem, REWARD_SAVE_NAME)
        torch.save(self.next_state_mem, NEXT_STATE_SAVE_NAME)
        torch.save(self.done_mem, DONE_SAVE_NAME)

    def load(self):
        """Function to load a replay buffer from file
        """
        self.sate_mem = torch.load(STATE_SAVE_NAME)
        self.action_mem = torch.load(ACTION_SAVE_NAME)
        self.reward_mem = torch.load(REWARD_SAVE_NAME)
        self.next_state_mem = torch.load(NEXT_STATE_SAVE_NAME)
        self.done_mem = torch.load(DONE_SAVE_NAME)
