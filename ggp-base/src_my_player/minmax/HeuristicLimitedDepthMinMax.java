package minmax;

import heuristics.StateClassifier;
import heuristics.StateClassifier.ClassificationException;

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

import state.MyState;

public class HeuristicLimitedDepthMinMax implements LimitedDepthMinMax{
	
	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private StateClassifier classifier;
	private int searchDepth;
	private Map<MyState, MinMaxEntry> cache;
	
	public HeuristicLimitedDepthMinMax(StateMachine machine, Role maxPlayer, StateClassifier classifier) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.classifier = classifier;
		this.searchDepth = 2;
		this.cache = new HashMap<MyState, MinMaxEntry>();
	}

	@Override
	public Move bestMove(MyState state) throws MinMaxException {
		if (state == null) {
			throw new MinMaxException();
		}
		try {
			return minmaxValueOf(state, searchDepth).getMove();
		} catch (GoalDefinitionException | MoveDefinitionException
				| TransitionDefinitionException | ClassificationException e) {
			throw new MinMaxException();
		}
	}
	
	private MinMaxEntry minmaxValueOf(MyState state, int depth)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException, ClassificationException {
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
			return new MinMaxEntry(goalValue, null, 10);
		} else if(depth < 0) {
			return new MinMaxEntry(classifier.classifyState(state), null, 0);
		} else if (maxPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = maxMove(state, depth);
		} else if (minPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = minMove(state, depth);
		} else {
			throw new RuntimeException(
					"minmax error: no match for controlingPlayer");
		}
		cache.put(state, minmaxEntry);
		return minmaxEntry;
	}

	private MinMaxEntry maxMove(MyState state, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException, ClassificationException {
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
			TransitionDefinitionException, GoalDefinitionException, ClassificationException {
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
