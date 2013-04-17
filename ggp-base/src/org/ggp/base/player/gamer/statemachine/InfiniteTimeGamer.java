package org.ggp.base.player.gamer.statemachine;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public class InfiniteTimeGamer extends StateMachineGamer {

	private static final int MAX_GOAL = 100;
	private static final int TIMEOUT_BUFFER = 500;
	
	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		StateMachine stateMachine = getStateMachine();
		List<Move> legalMoves = stateMachine.getLegalMoves(getCurrentState(), getRole());
		Map<Move, List<MachineState>> nextStates = stateMachine.getNextStates(getCurrentState(), getRole());
		
		Move bestMove = (legalMoves.get(new Random().nextInt(legalMoves.size()))); // at worst, we're a random player
		int bestValue = -1;
		
		for (Move move : legalMoves) {
			if (System.currentTimeMillis() > timeout - TIMEOUT_BUFFER) {
				break;
			}
			
			int minValue = MAX_GOAL;
			
			for (MachineState state : nextStates.get(move)) {
				int value = getStateValue(state, timeout);
				
				if (value < minValue) {
					minValue = value;
				}
			}
			
			if (minValue > bestValue) {
				bestMove = move;
				bestValue = minValue;
			}
		}
		
		return bestMove;
	}

	@Override
	public void stateMachineStop() {

	}

	@Override
	public void stateMachineAbort() {

	}

	@Override
	public void analyze(Game g, long timeout) throws GameAnalysisException {

	}

	@Override
	public String getName() {
		return "InfiniteTime";
	}
	
	private int getStateValue(MachineState currentState, long timeout) {		
		StateMachine stateMachine = getStateMachine();
		
		if (stateMachine.isTerminal(currentState)) {
			try {
				return stateMachine.getGoal(currentState, getRole());
			} catch (GoalDefinitionException e) {
				System.err.println("Error: Goal definition exception");
				e.printStackTrace();
				return -1;
			}
		}
		
		if (System.currentTimeMillis() > timeout - TIMEOUT_BUFFER) {
			return 0;
		}
		
		Map<Move, List<MachineState>> nextStates;
		List<Move> legalMoves;
		
		try {
			nextStates = stateMachine.getNextStates(currentState, getRole());
			legalMoves = stateMachine.getLegalMoves(currentState, getRole());
		} catch (MoveDefinitionException e) {
			System.err.println("Error: Move definition exception");
			e.printStackTrace();
			return -1;
		} catch (TransitionDefinitionException e) {
			System.err.println("Error: Transition definition exception");
			e.printStackTrace();
			return -1;
		}
		
		int bestValue = 0;
		
		for (Move move : legalMoves) {
			if (System.currentTimeMillis() > timeout - TIMEOUT_BUFFER) {
				break;
			}
			
			int minValue = MAX_GOAL;
			
			for (MachineState nextState : nextStates.get(move)) {
				int value = getStateValue(nextState, timeout);
				if (value < minValue) {
					minValue = value;
				}
			}
			
			if (minValue > bestValue) {
				bestValue = minValue;
			}
		}
		
		return bestValue;
	}

}
