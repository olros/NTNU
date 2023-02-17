# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from pacman import GameState
from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        return successorGameState.getScore()

def scoreEvaluationFunction(currentGameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

    def isTerminal(self, gameState: GameState, depth: int):
        """The game is terminal if it's lost, won or reached the depth"""
        return gameState.isLose() or gameState.isWin() or depth == self.depth

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def minValue(self, gameState: GameState, depth: int, ghostIndex = 1):
        # Return if the game is terminal
        if self.isTerminal(gameState, depth):
            return (self.evaluationFunction(gameState), None)

        # Initially the score of the best move is +infinity and there is no best move yet
        (v, action) = (float('inf'), None)

        # We'll have to check the minValue of all agents except the Pacman
        amountOfGhosts = gameState.getNumAgents() - 1

        # All legal moves should be explored
        legalActions = gameState.getLegalActions(ghostIndex)
        for action in legalActions:
            successorGameState = gameState.generateSuccessor(ghostIndex, action)

            # By recursivly running self.minValue with increasing ghostIndex, all ghosts will be evaluated
            allGhostsHaveBeenChecked = ghostIndex == amountOfGhosts
            if allGhostsHaveBeenChecked: # Time for Pacman to act
                v2, _ = self.maxValue(successorGameState, depth + 1)
            else: # Evaluate next ghost
                v2, _ = self.minValue(successorGameState, depth, ghostIndex + 1)

            # Update the best move if this move is better (gives lower score) then the existing
            if v2 < v:
                (v, action) = (v2, action)

        return (v, action)

    def maxValue(self, gameState: GameState, depth: int):
        # Return if the game is terminal
        if self.isTerminal(gameState, depth):
            return (self.evaluationFunction(gameState), None)

        # Initially the score of the best move is -infinity and there is no best move yet
        (v, move) = (float('-inf'), None)

        # All legal moves should be explored
        legalActions = gameState.getLegalActions(self.index)
        for action in legalActions:
            successorGameState = gameState.generateSuccessor(self.index, action)
            v2, _ =  self.minValue(successorGameState, depth)

            # Update the best move if this move is better (gives higher score) then the existing
            if v2 > v:
                (v, move) = (v2, action)

        return (v, move)


    def getAction(self, gameState: GameState):
        # The initial depth is 0
        _, bestMove = self.maxValue(gameState, 0)
        return bestMove
        

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def minValue(self, gameState: GameState, alpha: float, beta: float, depth: int, ghostIndex = 1):
        # Return if the game is terminal
        if self.isTerminal(gameState, depth):
            return (self.evaluationFunction(gameState), None)

        # Initially the score of the best move is +infinity and there is no best move yet
        (v, move) = (float('inf'), None)

        # We'll have to check the minValue of all agents except the Pacman
        amountOfGhosts = gameState.getNumAgents() - 1

        # All legal moves should be explored
        legalActions = gameState.getLegalActions(ghostIndex)
        for action in legalActions:
            successorGameState = gameState.generateSuccessor(ghostIndex, action)

            # By recursivly running self.minValue with increasing ghostIndex, all ghosts will be evaluated
            allGhostsHaveBeenChecked = ghostIndex == amountOfGhosts
            if allGhostsHaveBeenChecked: # Time for Pacman to act
                v2, _ = self.maxValue(successorGameState, alpha, beta, depth + 1)
            else: # Evaluate next ghost
                v2, _ = self.minValue(successorGameState, alpha, beta, depth, ghostIndex + 1)

            # Update the best move if this move is better (gives lower score) then the existing
            if v2 < v:
                (v, move) = (v2, action)

            beta = min(beta, v)
            # There is no need to expand the rest of the tree if beta now is lower then alpha
            if beta < alpha:
                return (v, move)

        return (v, move)
    
    def maxValue(self, gameState: GameState, alpha: float, beta: float, depth: int):
        # Return if the game is terminal
        if self.isTerminal(gameState, depth):
            return (self.evaluationFunction(gameState), None)

        # Initially the score of the best move is -infinity and there is no best move yet
        (v, move) = (float('-inf'), None)

        # All legal moves should be explored
        legalActions = gameState.getLegalActions(self.index)
        for action in legalActions:
            successorGameState = gameState.generateSuccessor(self.index, action)
            v2, _ =  self.minValue(successorGameState, alpha, beta, depth)

            # Update the best move if this move is better (gives higher score) then the existing
            if v2 > v:
                (v, move) = (v2, action)
            
            alpha = max(alpha, v)
            # There is no need to expand the rest of the tree if alpha now is higher then beta
            if alpha > beta:
                return (v, move)

        return (v, move)

    def getAction(self, gameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        infinity = float('inf')
        negativeInfinity = float('-inf')

        # The initial depth is 0
        _, bestMove = self.maxValue(gameState, negativeInfinity, infinity, 0)
        return bestMove

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    util.raiseNotDefined()

# Abbreviation
better = betterEvaluationFunction
