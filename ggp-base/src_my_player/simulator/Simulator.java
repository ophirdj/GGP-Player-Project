package simulator;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

/**
 * A class to make simple random simulations of a game (specified by an
 * initializing state machine). Class is thread-safe only if supplied with a
 * thread-safe state machine in initialization.
 * 
 * @author Ophir De Jager
 * 
 */
public class Simulator {

	protected StateMachine machine;
	protected List<Role> roles;

	public Simulator(StateMachine machine) {
		this.machine = machine;
		this.roles = machine.getRoles();
		assert (roles.size() == 2);
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
		while (!machine.isTerminal(machineState)) {
			machineState = machine.getRandomNextState(machineState);
			myState = new MyState(machineState, myState.getTurnNumber() + 1,
					getNextPlayer(controlingPlayer));
			simulation.add(myState);
		}
		return simulation;
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
			System.out.println("initial state is null");
		for (List<GdlTerm> nextMove : moveHistory) {
			List<Move> jointMove = new ArrayList<Move>();
			for (GdlTerm sentence : nextMove) {
				jointMove.add(machine.getMoveFromTerm(sentence));
			}
			state = machine.getNextState(state, jointMove);
		}
		if (state == null)
			System.out.println("state is null");
		return state;
	}

}
