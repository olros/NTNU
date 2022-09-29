import time

import numpy as np
import torch
from tqdm import tqdm

import wandb
from src.agents.ppo_agent import PPOAgent
from src.constants import (
    ACTOR_LEARNING_RATE,
    CHECKPOINT_AMOUNT,
    CLIP_RANGE,
    CRITIC_LEARNING_RATE,
    EPISODES,
    MIN_WANDB_VIDEO_REWARD,
    OPTIMIZER_EPSILON,
    PPO_EPOCHS,
    PPO_GAMMA,
    PPO_LAMBDA,
    STEP_AMOUNT,
    UPDATE_FREQUENCY,
    WANDB_ENTITY,
    WANDB_PPO_PROJECT,
)
from src.environment import create_mario_env


def run(pretrained: bool, num_episodes: int = EPISODES, wandb_name: str = None):
    """Function to run and train a ppo agent

    Args:
        pretrained (bool): boolean that sys if we want to load a previous agent or not
        num_episodes (int, optional): the number of episodes to train for. Defaults to EPISODES from constants.
        wandb_name (str, optional): the name of wandb logging session. Defaults to None, if not None logging to wandb is performed.
    """

    should_log = bool(wandb_name)

    if should_log:
        wandb.init(
            project=WANDB_PPO_PROJECT,
            name=wandb_name,
            entity=WANDB_ENTITY,
            config={
                "ACTOR_LEARNING_RATE": ACTOR_LEARNING_RATE,
                "CLIP_RANGE": CLIP_RANGE,
                "CRITIC_LEARNING_RATE": CRITIC_LEARNING_RATE,
                "OPTIMIZER_EPSILON": OPTIMIZER_EPSILON,
                "STEP_AMOUNT": STEP_AMOUNT,
                "UPDATE_FREQUENCY": UPDATE_FREQUENCY,
                "EPISODES": num_episodes,
                "PPO_GAMMA": PPO_GAMMA,
                "PPO_LAMBDA": PPO_LAMBDA,
                "PPO_EPOCHS": PPO_EPOCHS,
            },
        )

    env = create_mario_env()
    state_space = env.observation_space.shape
    action_space = env.action_space.n
    agent = PPOAgent(state_space, action_space)
    flags = []
    if pretrained:
        agent.load()
    episodic_reward = []
    max_episode_reward = 0
    play_episode = 1
    flags = 0
    for ep_num in tqdm(range(num_episodes)):
        total_reward = []
        frames = []
        states = np.zeros((STEP_AMOUNT, 4, 84, 84), dtype=np.float32)
        actions = np.zeros(STEP_AMOUNT, dtype=np.int32)
        rewards = np.zeros(STEP_AMOUNT, dtype=np.float32)
        dones = np.zeros(STEP_AMOUNT, dtype=bool)
        prev_log_probs = np.zeros(STEP_AMOUNT, dtype=np.float32)
        values = np.zeros(STEP_AMOUNT, dtype=np.float32)
        state = env.reset()
        reward = 0
        for step in range(STEP_AMOUNT):
            states[step] = state
            actions[step], values[step], prev_log_probs[step] = agent.act(state)
            state, rewards[step], dones[step], info = env.step(actions[step])
            frames.append(env.frame)
            episodic_reward.append(rewards[step])
            reward += rewards[step]
            if dones[step]:
                if info["flag_get"]:
                    flags += 1
                total_episode_reward = np.sum(episodic_reward)
                total_reward.append(reward)
                if total_episode_reward > max_episode_reward:
                    max_episode_reward = total_episode_reward
                    if should_log and total_episode_reward > MIN_WANDB_VIDEO_REWARD:
                        try:

                            wandb.log(
                                {
                                    "video": wandb.Video(
                                        np.stack(frames, 0).transpose(0, 3, 1, 2),
                                        str(reward),
                                        fps=25,
                                        format="mp4",
                                    )
                                }
                            )
                        except Exception:
                            tqdm.write("something happend while logging")

                if should_log:
                    wandb.log(
                        {
                            "mean_last_10_episodes": np.mean(total_reward[-10:]),
                            "episode_reward": reward,
                            "flag_count": flags,
                        },
                        step=play_episode,
                    )

                episodic_reward = []
                play_episode += 1
                frames = []
                reward = 0
                env.reset()

        # Adds the an extra value so you can calculate advantages with
        # rewards[i] + self.gamma * values[i + 1] * mask - values[i]
        _, last_value, _ = agent.act(state)
        values = np.append(values, last_value)
        states = torch.tensor(states.reshape(states.shape[0], *states.shape[1:]))
        agent.train(
            to_tensor(states),
            to_tensor(actions),
            to_tensor(rewards),
            to_tensor(dones),
            to_tensor(prev_log_probs),
            to_tensor(values),
        )

        if ep_num % CHECKPOINT_AMOUNT == 0:
            agent.save()

        tqdm.write(
            "Mean total reward after episode {} is {}".format(
                ep_num, np.mean(total_reward)
            )
        )


def to_tensor(list):
    list = torch.tensor(list, device="cuda" if torch.cuda.is_available() else "cpu")
    return list


def play(load_file: str = None):
    """Function to load a ppo agent and play the environment

    Args:
        load_file (str, optional): name of model file to load. Defaults to None, if None uses agent load function.
    """
    env = create_mario_env()
    state_space = env.observation_space.shape
    action_space = env.action_space.n
    agent = PPOAgent(state_space, action_space)
    if load_file:
        agent.model = torch.load(load_file)
    else:
        agent.load()
    state = env.reset()
    while True:
        action = agent.play(state)
        state_next, _, done, _ = env.step(action.item())
        env.render()
        time.sleep(0.05)
        state = state_next
        if done:
            break
    env.close()
