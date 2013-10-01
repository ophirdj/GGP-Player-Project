package minmax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import debugging.Verbose;

import state.MyState;

public class NoHeuristicLimitedDepthMinMax implements LimitedDepthMinMax{
	
	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private int searchDepth;
	private Map<MyState, MinMaxEntry> cache;
	
	public NoHeuristicLimitedDepthMinMax(StateMachine machine, Role maxPlayer) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.searchDepth = 2;
		this.cache = new HashMap<MyState, MinMaxEntry>();
	}

	@Override
	public Move bestMove(MyState state) throws MinMaxException {
		if (state == null) {
			throw new MinMaxException();
		}
		try {
			Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
			return minmaxValueOf(state, searchDepth).getMove();
		} catch (GoalDefinitionException | MoveDefinitionException
				| TransitionDefinitionException e) {
			e.printStackTrace();
			throw new MinMaxException();
		}
	}
	
	private MinMaxEntry minmaxValueOf(MyState state, int depth)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException {
		if (cache.containsKey(state) && cache.get(state) != null) {
			return cache.get(state);
		} else if (cache.containsKey(state)) {
			return null;
		}
		MinMaxEntry minmaxEntry = null;
		cache.put(state, null);
		if (machine.isTerminal(state.getState())) {
			double goalValue = (machine.getGoal(state.getState(), maxPlayer)
					- machine.getGoal(state.getState(), minPlayer)) * 10000;
			Verbose.printVerbose("Final State with goal value " + goalValue, Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(goalValue, null, 10);
		} else if(depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(0, null, 0);
		} else if (maxPlayer.equals(state.getControlingPlayer())) {
			Verbose.printVerbose("MAX PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = maxMove(state, depth);
		} else if (minPlayer.equals(state.getControlingPlayer())) {
			Verbose.printVerbose("MIN PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = minMove(state, depth);
		} else {
			throw new RuntimeException(
					"minmax error: no match for controlingPlayer");
		}
		cache.put(state, minmaxEntry);
		return minmaxEntry;
	}

	private MinMaxEntry maxMove(MyState state, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		MinMaxEntry maxEntry = null;
		for (Entry<Move, List<MachineState>> maxMove : machine.getNextStates(
				state.getState(), maxPlayer).entrySet()) {
			assert (maxMove.getValue().size() == 1);
			MachineState nextMachineState = maxMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, minPlayer);
			MinMaxEntry nextEntry = minmaxValueOf(nextState, depth - 1);
			if ((maxEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() > maxEntry
							.getValue()))) {
				maxEntry = new MinMaxEntry(nextEntry.getValue(),
						maxMove.getKey(), nextEntry.getImportance() + 1);
			}
		}
		return maxEntry;
	}

	private MinMaxEntry minMove(MyState state, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		MinMaxEntry minEntry = null;
		for (Entry<Move, List<MachineState>> minMove : machine.getNextStates(
				state.getState(), minPlayer).entrySet()) {
			assert (minMove.getValue().size() == 1);
			MachineState nextMachineState = minMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, maxPlayer);
			MinMaxEntry nextEntry = minmaxValueOf(nextState, depth - 1);
			if ((minEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() < minEntry
							.getValue()))) {
				minEntry = new MinMaxEntry(nextEntry.getValue(),
						minMove.getKey(), nextEntry.getImportance() + 1);
			}
		}
		return minEntry;
	}

	@Override
	public void setDepth(int depth) {
		this.searchDepth = depth;
	}
}
