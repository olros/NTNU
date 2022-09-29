from typing import Any, Tuple

import numpy as np
import torch
from torch import nn

from src.constants import (
    ACTOR_LEARNING_RATE,
    CLIP_RANGE,
    CRITIC_LEARNING_RATE,
    OPTIMIZER_EPSILON,
    PPO_EPOCHS,
    PPO_GAMMA,
    PPO_LAMBDA,
    STEP_AMOUNT,
    UPDATE_FREQUENCY,
)
from src.models.ppo import PPO


class PPOAgent:
    def __init__(self, state_shape, action_shape):
        self.gamma = PPO_GAMMA
        self.lamda = PPO_LAMBDA
        self.epochs = PPO_EPOCHS
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.policy = PPO(state_shape, action_shape).to(self.device)
        self.policy_old = PPO(state_shape, action_shape).to(self.device)
        self.policy_old.load_state_dict(self.policy.state_dict())
        self.loss_func = nn.MSELoss().to(self.device)
        self.optimizer = torch.optim.Adam(
            [
                {"params": self.policy.actor.parameters(), "lr": ACTOR_LEARNING_RATE},
                {"params": self.policy.critic.parameters(), "lr": CRITIC_LEARNING_RATE},
            ],
            eps=OPTIMIZER_EPSILON,
        )

    def save(self):
        """Function to save the policy network
        """
        self.policy.save()

    def load(self):
        """Function to load the policy network from the given file defined in constants,
        and loading the policy weights over to the target policy
        """
        self.policy.load(device=self.device)
        self.policy_old.load_state_dict(self.policy.state_dict())

    def play(self, state: torch.Tensor) -> int:
        """Function to preform an action by the actor network

        Args:
            state (torch.Tensor): the given state to calculate the aciton from

        Returns:
            int: the best action given the state
        """
        pi, _ = self.policy(
            torch.tensor(state, dtype=torch.float32, device=self.device).unsqueeze(0)
        )
        action = pi.sample()
        return action

    def act(self, state: torch.Tensor) -> Tuple:
        """Function to preform an action in the environment given a state

        Args:
            state (torch.Tensor): the given state to calculate the action from

        Returns:
            Tuple: a tuple with the action form athe actor, the given q value form the critic,
            and the log prob given the actor action
        """
        with torch.no_grad():
            pi, v = self.policy(
                torch.tensor(state, dtype=torch.float32, device=self.device).unsqueeze(
                    0
                )
            )
            value = v.cpu().numpy()
            action = pi.sample()
            return action, value, pi.log_prob(action).cpu().numpy()

    def calculate_advantages(
        self, rewards: torch.Tensor, dones: torch.Tensor, values: torch.Tensor
    ) -> Tuple:
        """Function to calculate the advantages from a given set of samples by Generalized Advantage Estimation

        Args:
            rewards (torch.Tensor): the sample rewards
            dones (torch.Tensor): array with booleans that says if the action ended the round or not
            values (torch.Tensor): the given q-values from the critic network

        Returns:
            Tuple: the given returns from calculating the advantages, and the given advanteges
        """
        returns = []
        gae = 0
        npValues = values.cpu().numpy()
        for i in reversed(range(len(rewards))):
            mask = 1.0 - int(dones[i])
            delta = (
                rewards.cpu().numpy()[i]
                + self.gamma * npValues[i + 1] * mask
                - npValues[i]
            )
            gae = delta + self.gamma * self.lamda * mask * gae
            returns.insert(0, gae + npValues[i])
        returns = np.array(returns)
        adv = returns - npValues[:-1]
        return (
            torch.tensor(returns, device=self.device, dtype=torch.float32),
            torch.tensor(
                (adv - np.mean(adv)) / (np.std(adv) + 1e-8),
                device=self.device,
                dtype=torch.float32,
            ),
        )

    def calculate_loss(
        self,
        states: torch.Tensor,
        actions: torch.Tensor,
        prev_log_probs: torch.Tensor,
        returns: torch.Tensor,
        advantages: torch.Tensor,
    ) -> Any:
        """Function to calculate the loss by the ratio between the old and current policy and the advantages,
        and clipping said advantages between 1+clip_range and 1-clip_range

        Args:
            states (torch.Tensor): given states for the training batch
            actions (torch.Tensor): given action for the training batch
            prev_log_probs (torch.Tensor): given old log probs give by the old policy
            returns (torch.Tensor): the given return of the advantage calculation
            advantages (torch.Tensor): the advantages from the traning batch

        Returns:
            Any: the new loss to update the model with
        """
        pi, value = self.policy(states)
        ratio = torch.exp(pi.log_prob(actions) - prev_log_probs)
        clipped_ratio = ratio.clamp(min=1 - CLIP_RANGE, max=1 + CLIP_RANGE)
        policy_reward = torch.min(ratio * advantages, clipped_ratio * advantages)
        entropy_bonus = pi.entropy()
        mse_loss = self.loss_func(value, returns)
        loss = -policy_reward + 0.5 * mse_loss - 0.01 * entropy_bonus
        return loss.mean()

    def train(
        self,
        states: torch.Tensor,
        actions: torch.Tensor,
        rewards: torch.Tensor,
        dones: torch.Tensor,
        prev_log_probs: torch.Tensor,
        values: torch.Tensor,
    ):
        """Function to train the model on collected samples

        Args:
            states (torch.Tensor): state samples
            actions (torch.Tensor): action samples
            rewards (torch.Tensor): reward samples
            dones (torch.Tensor):  array with booleans that says if the action ended the round or not
            prev_log_probs (torch.Tensor): the log prob samples
            values (torch.Tensor): the critic q value samples
        """
        returns, advantages = self.calculate_advantages(rewards, dones, values)
        indexes = torch.randperm(STEP_AMOUNT)
        step_amount = STEP_AMOUNT // UPDATE_FREQUENCY
        for batch_start in range(0, STEP_AMOUNT, step_amount):
            batch_end = batch_start + step_amount
            batch_indexes = indexes[batch_start:batch_end]
            for _ in range(self.epochs):
                loss = self.calculate_loss(
                    states[batch_indexes],
                    actions[batch_indexes],
                    prev_log_probs[batch_indexes],
                    returns[batch_indexes],
                    advantages[batch_indexes],
                )
                self.optimizer.zero_grad()
                loss.backward()
                self.optimizer.step()
            self.policy_old.load_state_dict(self.policy.state_dict())
