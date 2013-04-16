package org.ggp.base.player.gamer.statemachine;

import java.util.List;
import java.util.Map;

import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

/*
 * The CautiousGamer chooses the next legal move which will maximize the
 * average of the difference between his utility and his opponents' average
 * utility in each of the successive states.
 */

public class CautiousGamer extends StateMachineGamer {
	private final int MAX_STATE_VALUE = 100;
	private final int MIN_STATE_VALUE = 0;
		
	private final int LATENCY_BUFFER_TIME_MILLIS = 500;

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		/*TODO: Implement metagaming*/
	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		StateMachine stateMachine = getStateMachine();
		
		Role currentRole = getRole();
		MachineState currentState = getCurrentState();
		
		List<Role> opponentRoles = stateMachine.getRoles();
		opponentRoles.remove(currentRole);
		
		List<Move> legalMoves = stateMachine.getLegalMoves(currentState, currentRole);
		Map<Move, List<MachineState>> nextStates = stateMachine.getNextStates(currentState, currentRole);
		
		Move bestMove = legalMoves.get(0);
		double maxPlayerUtility = MIN_STATE_VALUE;
		
		while(System.currentTimeMillis() < timeout - LATENCY_BUFFER_TIME_MILLIS) {
			for (Move legalMove : legalMoves) {
				double avgPlayerUtility = MIN_STATE_VALUE;
				List<MachineState> successiveStates = nextStates.get(legalMove);
				
				for (MachineState nextState : successiveStates) {
					double playerUtility = stateMachine.getGoal(nextState, currentRole);
					double avgOpponentUtility = MIN_STATE_VALUE;
					
					if (!opponentRoles.isEmpty()) {
						int totalOpponentUtility = 0;
						
						for(Role opponent : opponentRoles) {
							totalOpponentUtility += stateMachine.getGoal(nextState, opponent);
						}
						
						avgOpponentUtility = totalOpponentUtility / opponentRoles.size();
					}
					
					avgPlayerUtility += playerUtility - avgOpponentUtility;
				}
				
				avgPlayerUtility /= successiveStates.size();
				
				if (avgPlayerUtility > maxPlayerUtility) {
					maxPlayerUtility = avgPlayerUtility;
					bestMove = legalMove;
				}
			}
		}
		
		System.out.println("Player utility: " + maxPlayerUtility);
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
		return "CautiousGamer";
	}
}
