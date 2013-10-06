package minmax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

/**
 * A simple class for computing the minmax value of game states. Note: the
 * calculated value is indeed the real minmax value, meaning that the whole game
 * tree from the given state will be discovered. Use carefully or wait forever
 * for the result. For efficiency, the class has a cache to save minmax values
 * of already explored states.
 * 
 * @author Ophir De Jager
 * 
 */
public class NoHeuristicCachedMinMax implements MinMax {

	private static final int CACHE_HEIGHT_THRESHOLD = 1;
	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private Map<MyState, MinMaxEntry> cache;
	private MinMaxEventReporter reporter;
	private int searchDepth;


	public NoHeuristicCachedMinMax(StateMachine machine, Role maxPlayer) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.cache = new HashMap<MyState, MinMaxEntry>();
		this.reporter = new MinMaxEventReporter();
	}

	public void pruneCache() {
		Set<MyState> toBeDeleted = new HashSet<MyState>();
		for (Entry<MyState, MinMaxEntry> entry : cache.entrySet()) {
			if (entry.getValue() != null && entry.getValue().canPrune()) {
				toBeDeleted.add(entry.getKey());
			} else if (entry.getValue() != null) {
				entry.getValue().reduceTTL();
			}
		}
		for (MyState key : toBeDeleted) {
			cache.remove(key);
		}
	}

	public Move bestMove(MyState state) throws MinMaxException, GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (state == null) {
			throw new MinMaxException();
		}
		long startTime = System.currentTimeMillis();
		searchDepth = 0;
		Move move = minmaxValueOf(state, 0).getMove();
		long endTime = System.currentTimeMillis();
		reporter.reportAndReset(move, cache.size(), searchDepth, endTime - startTime);
		return move;
	}

	public Double valuOf(MyState state) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException, MinMaxException {
		if (state == null) {
			return null;
		}
		return minmaxValueOf(state, 0).getValue();
	}

	private MinMaxEntry minmaxValueOf(MyState state, int depth)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException, MinMaxException {
		reporter.exploreNode();
		if(depth > searchDepth) {
			searchDepth = depth;
		}
		if (cache.containsKey(state) && cache.get(state) != null) {
			return cache.get(state);
		} else if (cache.containsKey(state)) {
			return null;
		}
		MinMaxEntry minmaxEntry = null;
		cache.put(state, null);
		if (machine.isTerminal(state.getState())) {
			return new MinMaxEntry(machine.getGoal(state.getState(), maxPlayer)
					- machine.getGoal(state.getState(), minPlayer), null, 0);
		} else if (maxPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = maxMove(state, depth);
		} else if (minPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = minMove(state, depth);
		} else {
			throw new MinMaxException(
					"minmax error: no match for controlingPlayer");
		}
		if (minmaxEntry.getImportance() >= CACHE_HEIGHT_THRESHOLD) {
			cache.put(state, minmaxEntry);
		}
		return minmaxEntry;
	}

	private MinMaxEntry maxMove(MyState state, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException, MinMaxException {
		MinMaxEntry maxEntry = null;
		Set<Entry<Move, List<MachineState>>> children = machine.getNextStates(
				state.getState(), maxPlayer).entrySet();
		reporter.expandNode(children.size());
		for (Entry<Move, List<MachineState>> maxMove : children) {
			assert (maxMove.getValue().size() == 1);
			MachineState nextMachineState = maxMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, minPlayer);
			MinMaxEntry nextEntry = minmaxValueOf(nextState, depth + 1);
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
			TransitionDefinitionException, GoalDefinitionException, MinMaxException {
		MinMaxEntry minEntry = null;
		Set<Entry<Move, List<MachineState>>> children = machine.getNextStates(
				state.getState(), minPlayer).entrySet();
		reporter.expandNode(children.size());
		for (Entry<Move, List<MachineState>> minMove : children) {
			assert (minMove.getValue().size() == 1);
			MachineState nextMachineState = minMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, maxPlayer);
			MinMaxEntry nextEntry = minmaxValueOf(nextState, depth + 1);
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
	public void clear() {
		cache.clear();
	}
	
	@Override
	public void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

}
