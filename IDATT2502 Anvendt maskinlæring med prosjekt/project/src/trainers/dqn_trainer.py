import time

import numpy as np
import torch
from tqdm import tqdm

import wandb
from src.agents.ddqn_agent import DoubleDQNAgent
from src.agents.dqn_agent import DQNAgent
from src.constants import (
    BATCH_SIZE,
    CHECKPOINT_AMOUNT,
    COPY_STEPS,
    ENDING_POSISTION_PICKLE,
    EPISODES,
    EPSILON,
    EPSILON_DECAY_RATE,
    GAMMA,
    LEARNING_RATE,
    MEMORY_SIZE,
    MIN_EPSILON,
    MIN_WANDB_VIDEO_REWARD,
    NUM_IN_QUEUE_PICKLE,
    TOTAL_REWARDS_PICKLE,
    WANDB_DDQN_PROJECT,
    WANDB_DQN_PROJECT,
    WANDB_ENTITY,
)
from src.environment import create_mario_env


def run(
    pretrained: bool,
    num_episodes: int = EPISODES,
    double: bool = True,
    wandb_name: str = None,
):
    """Function to run and train a dqn or ddqn agent

    Args:
        pretrained (bool): boolean that sys if we want to load a previous agent or not
        num_episodes (int, optional): the number of episodes to train for. Defaults to EPISODES from constants.
        double (bool, optional): boolean that says if the agent is going to be a ddqn agent or dqn agent. Defaults to True.
        wandb_name (str, optional): the name of wandb logging session. Defaults to None, if not None logging to wandb is performed.
    """
    should_log = bool(wandb_name)

    if should_log:
        wandb.init(
            project=WANDB_DDQN_PROJECT if double else WANDB_DQN_PROJECT,
            name=wandb_name,
            entity=WANDB_ENTITY,
            config={
                "BATCH_SIZE": BATCH_SIZE,
                "COPY_STEPS": COPY_STEPS,
                "ENDING_POSISTION_PICKLE": ENDING_POSISTION_PICKLE,
                "EPSILON": EPSILON,
                "EPSILON_DECAY_RATE": EPSILON_DECAY_RATE,
                "GAMMA": GAMMA,
                "LEARNING_RATE": LEARNING_RATE,
                "MEMORY_SIZE": MEMORY_SIZE,
                "MIN_EPSILON": MIN_EPSILON,
                "NUM_IN_QUEUE_PICKLE": NUM_IN_QUEUE_PICKLE,
                "TOTAL_REWARDS_PICKLE": TOTAL_REWARDS_PICKLE,
                "EPISODES": num_episodes,
            },
        )

    env = create_mario_env()
    state_space = env.observation_space.shape
    action_space = env.action_space.n
    agent = (
        DoubleDQNAgent(env, state_space, action_space)
        if double
        else DQNAgent(env, state_space, action_space)
    )
    if pretrained:
        agent.load()

    total_rewards = []
    max_episode_reward = 0
    flags = 0
    for ep_num in tqdm(range(num_episodes)):
        state = env.reset()
        frames = []
        state = torch.tensor(np.array([state]))
        total_reward = 0
        steps = 0
        while True:
            action = agent.act(state)
            steps += 1

            state_next, reward, done, info = env.step(action)
            frames.append(env.frame)
            total_reward += reward
            state_next = torch.tensor(np.array([state_next]))
            reward = torch.tensor(np.array([reward])).unsqueeze(0)

            done = torch.tensor(np.array([int(done)])).unsqueeze(0)
            action = torch.tensor(np.array([int(action)])).unsqueeze(0)

            agent.remember(state, action, reward, state_next, done)
            agent.replay()

            state = state_next
            if done:
                if info["flag_get"]:
                    flags += 1
                if total_reward > max_episode_reward:
                    try:
                        max_episode_reward = total_reward
                        if should_log and total_reward > MIN_WANDB_VIDEO_REWARD:
                            wandb.log(
                                {
                                    "video": wandb.Video(
                                        np.stack(frames, 0).transpose(0, 3, 1, 2),
                                        str(total_reward),
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
                            "mean_last_10_episodes": np.mean(total_rewards[-10:]),
                            "episode_reward": np.sum(total_reward),
                            "flag_count": flags,
                        },
                        step=ep_num,
                    )

                break

        total_rewards.append(total_reward)
        if ep_num & CHECKPOINT_AMOUNT == 0:
            agent.save(total_rewards)

        tqdm.write(
            "Total reward after episode {} is {}".format(ep_num + 1, total_reward)
        )
    agent.save(total_rewards)
    env.close()


def play(double=True, load_file: str = None):
    """Function to play a trained dqn or ddqn model

    Args:
        double (bool, optional):  if the agent playing is a ddqn or dqn agent. Defaults to True.
        load_file (str, optional): name of model file to load. Defaults to None, if None uses agent load function.
    """
    env = create_mario_env()
    state_space = env.observation_space.shape
    action_space = env.action_space.n
    agent = (
        DoubleDQNAgent(env, state_space, action_space)
        if double
        else DQNAgent(env, state_space, action_space)
    )
    if load_file:
        agent.model = torch.load(load_file)
    else:
        agent.model.load(agent.device)

    state = env.reset()
    state = torch.Tensor(np.array([state]))
    while True:
        action = agent.play(state)
        state_next, _, done, _ = env.step(action.item())
        env.render()
        time.sleep(0.05)
        state_next = torch.Tensor(np.array([state_next]))
        state = state_next
        if done:
            break
    env.close()
