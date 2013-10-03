package simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import debugging.TicTacToeBoardPrint;
import debugging.Verbose;

import state.MyState;

/**
 * A class to make simple random simulations of a game (specified by an
 * initializing state machine). Class is thread-safe only if supplied with a
 * thread-safe state machine in initialization.
 * 
 * @author Ophir De Jager
 * 
 */
public class KnownValueSimulator implements MapValueSimulator{
	
	private static int NO_VALUE_FOUND = Integer.MIN_VALUE;
	
	protected StateMachine machine;
	protected List<Role> roles;
	protected Map<MyState, Integer> knownStates;
	protected Role myRole;
	protected Role oponentRole;

	public KnownValueSimulator(StateMachine machine, Role myRole, Role oponentRole) {
		this.machine = machine;
		this.myRole = myRole;
		this.oponentRole = oponentRole;
		this.roles = machine.getRoles();
		assert (roles.size() == 2);
		knownStates = new HashMap<MyState, Integer>();
	}

	/**
	 * Simulates a randomly played game from initial state, See also
	 * simulate(MachineState)
	 * 
	 * @param controlingPlayer
	 *            the role of the player currently playing
	 * @return A list of the simulation states ordered from the first (initial)
	 *         state to the last (terminal) state
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	public final List<MyState> simulate(Role controlingPlayer)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		return simulate(new ArrayList<List<GdlTerm>>(), controlingPlayer);
	}

	/**
	 * Simulates a randomly played game from given state
	 * 
	 * @param moveHistory
	 *            The move history from the beginning of the game
	 * @param controlingPlayer
	 *            the role of the player currently playing
	 * @return A list of the simulation states ordered from the first (given)
	 *         state to the last (terminal) state
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	public List<MyState> simulate(List<List<GdlTerm>> moveHistory,
			Role controlingPlayer) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		MachineState machineState = getStateFromMoveHistory(moveHistory);
		MyState myState = new MyState(machineState, moveHistory.size(),
				controlingPlayer);
		List<MyState> simulation = new ArrayList<MyState>();
		simulation.add(myState);
		while (!(knownStates.containsKey(myState) || machine.isTerminal(machineState))) {
			machineState = machine.getRandomNextState(machineState);
			myState = new MyState(machineState, myState.getTurnNumber() + 1,
					getNextPlayer(controlingPlayer));
			simulation.add(myState);
		}
		addStatesToKnown(simulation);
		return simulation;
	}

	private void addStatesToKnown(List<MyState> simulation) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		int lastIndex = simulation.size();
		ListIterator<MyState> reverseIterator = simulation.listIterator(lastIndex); 
		MyState finalState = reverseIterator.previous();
		if (!knownStates.containsKey(finalState) && machine.isTerminal(finalState.getState())){
			knownStates.put(finalState, finalState.evaluateTerminalState(machine, myRole));
		}
		while (reverseIterator.hasPrevious()){
			MyState currentState = reverseIterator.previous();
			Integer minMaxValue = checkChildrens(currentState);
			if(minMaxValue == NO_VALUE_FOUND){
				break;
			}
			knownStates.put(currentState, minMaxValue);
			Verbose.printVerbose("Found state with value " + minMaxValue + " and depth " + currentState.getTurnNumber() +": ", Verbose.SIMULATOR_MIN_MAX);
			TicTacToeBoardPrint.printTicTacToe(currentState.getState().getContents(), Verbose.TIC_TAC_TOE_SIMULATOR);
		}
	}

	private Integer checkChildrens(MyState currentState) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		List<MachineState> states = machine.getNextStates(currentState.getState());
		Role currentRole = currentState.getControlingPlayer();
		boolean allChildrenValuesKnown = true;
		int bestResult = currentRole.equals(myRole) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
		for (MachineState state : states){
			if (knownStates.containsKey(state)){
				bestResult = betterValue(currentRole, bestResult, knownStates.get(state));
			}
			else if (machine.isTerminal(state)){
				MyState terminalState = new MyState(state, currentState.getTurnNumber() + 1, getNextPlayer(currentState.getControlingPlayer()));
				int stateValue = terminalState.evaluateTerminalState(machine, myRole);
				knownStates.put(terminalState, stateValue);
				bestResult = betterValue(currentRole, bestResult, stateValue);
			}
			else{
				allChildrenValuesKnown = false;
			}
		}
		if(allChildrenValuesKnown) return bestResult;
		return NO_VALUE_FOUND;
	}

	private int betterValue(Role currentRole, int firstVal, int secondVal) {
		if (currentRole.equals(myRole)) return Math.max(firstVal, secondVal);
		return Math.min(firstVal, secondVal);
	}

	private Role getNextPlayer(Role controlingPlayer) {
		return roles.get(0).equals(controlingPlayer) ? roles.get(1) : roles
				.get(0);
	}

	/**
	 * Plays the move history in the state machine and returns the state it
	 * leads to
	 * 
	 * @param moveHistory
	 *            The move history from the beginning of the game
	 * @return The state the move history leads to
	 * @throws TransitionDefinitionException
	 *             If state machine threw this exception
	 */
	protected final MachineState getStateFromMoveHistory(
			List<List<GdlTerm>> moveHistory)
			throws TransitionDefinitionException {
		MachineState state = machine.getInitialState();
		if (state == null)
			return null;
		for (List<GdlTerm> nextMove : moveHistory) {
			List<Move> jointMove = new ArrayList<Move>();
			for (GdlTerm sentence : nextMove) {
				jointMove.add(machine.getMoveFromTerm(sentence));
			}
			state = machine.getNextState(state, jointMove);
		}
		return state;
	}

	@Override
	public Map<MyState, Integer> getStateValueMap() {
		return knownStates;
	}
	
	

}
