# from src.constants import EPISODES, GAMMA, MIN_EPSILON
from src.trainers.dqn_trainer import run as DQN_run

# from src.constants import EPISODES, PPO_GAMMA, PPO_LAMBDA
# from src.trainers.ppo_trainer import play
# from src.trainers.ppo_trainer import run as PPO_Run

DQN_run(pretrained=False)
# PPO_Run(
#     pretrained=False,
#     wandb_name=f"PPO-run-{EPISODES}-eps-gamma-{PPO_GAMMA}-lambda-{PPO_LAMBDA}-final-run",
# )
