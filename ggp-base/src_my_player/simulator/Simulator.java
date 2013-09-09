package simulator;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

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

	public Simulator(StateMachine machine) {
		this.machine = machine;
	}

	/**
	 * Simulates a randomly played game from initial state, See also
	 * simulate(MachineState)
	 * 
	 * @return A list of the simulation states ordered from the first (initial)
	 *         state to the last (terminal) state
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException 
	 */
	public final List<MachineState> simulate() throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		return simulate(new ArrayList<List<GdlTerm>>());
	}

	/**
	 * Simulates a randomly played game from given state
	 * 
	 * @param moveHistory
	 *            The move history from the beginning of the game
	 * @return A list of the simulation states ordered from the first (given)
	 *         state to the last (terminal) state
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws GoalDefinitionException
	 */
	public List<MachineState> simulate(List<List<GdlTerm>> moveHistory)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		MachineState state = getStateFromMoveHistory(moveHistory);
		List<MachineState> simulation = new ArrayList<MachineState>();
		simulation.add(state);
		while (!machine.isTerminal(state)) {
			state = machine.getRandomNextState(state);
			simulation.add(state);
		}
		return simulation;
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
		if(state == null) System.out.println("initial state is null");
		for (List<GdlTerm> nextMove : moveHistory) {
			List<Move> jointMove = new ArrayList<Move>();
			for (GdlTerm sentence : nextMove) {
				jointMove.add(machine.getMoveFromTerm(sentence));
			}
			state = machine.getNextState(state, jointMove);
		}
		if(state == null) System.out.println("state is null");
		return state;
	}

}
