package minmax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public class MinMax {

	private static final int CACHE_HEIGHT_THRESHOLD = 1;
	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private Map<MyState, CacheEntry> cache;

	private static class CacheEntry {

		private static final int STARTING_TTL = 0;
		private final int value;
		private final Move bestMove;
		private final int height;
		private int ttl;

		public CacheEntry(int value, Move bestMove, int height) {
			this.value = value;
			this.bestMove = bestMove;
			this.height = height;
			this.ttl = STARTING_TTL + height;
		}

		public void reduceTTL() {
			ttl--;
		}

		public boolean canPrune() {
			return ttl < CACHE_HEIGHT_THRESHOLD;
		}

		public int getValue() {
			ttl = STARTING_TTL + height;
			return value;
		}

		public Move getMove() {
			return bestMove;
		}

		public int getHeight() {
			return height;
		}
	}

	public MinMax(StateMachine machine, Role maxPlayer) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.cache = new HashMap<MyState, CacheEntry>();
	}

	public void pruneCache() {
		Set<MyState> toBeDeleted = new HashSet<MyState>();
		for (Entry<MyState, CacheEntry> entry : cache.entrySet()) {
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

	public Move bestMove(MyState state) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException {
		if (state == null) {
			return null;
		}
		return minmaxValueOf(state).getMove();
	}

	public Integer valuOf(MyState state) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException {
		if (state == null) {
			return null;
		}
		return minmaxValueOf(state).getValue();
	}

	private CacheEntry minmaxValueOf(MyState state)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException {
		if (cache.containsKey(state) && cache.get(state) != null) {
			return cache.get(state);
		} else if (cache.containsKey(state)) {
			return null;
		}
		CacheEntry minmaxEntry = null;
		cache.put(state, null);
		if (machine.isTerminal(state.getState())) {
			return new CacheEntry(machine.getGoal(state.getState(), maxPlayer)
					- machine.getGoal(state.getState(), minPlayer), null, 0);
		} else if (maxPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = maxMove(state);
		} else if (minPlayer.equals(state.getControlingPlayer())) {
			minmaxEntry = minMove(state);
		} else {
			throw new RuntimeException(
					"minmax error: no match for controlingPlayer");
		}
		if (minmaxEntry.getHeight() >= CACHE_HEIGHT_THRESHOLD) {
			cache.put(state, minmaxEntry);
		}
		return minmaxEntry;
	}

	private CacheEntry maxMove(MyState state) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		CacheEntry maxEntry = null;
		for (Entry<Move, List<MachineState>> maxMove : machine.getNextStates(
				state.getState(), maxPlayer).entrySet()) {
			assert (maxMove.getValue().size() == 1);
			MachineState nextMachineState = maxMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, minPlayer);
			CacheEntry nextEntry = minmaxValueOf(nextState);
			if ((maxEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() > maxEntry
							.getValue()))) {
				maxEntry = new CacheEntry(nextEntry.getValue(),
						maxMove.getKey(), nextEntry.getHeight() + 1);
			}
		}
		return maxEntry;
	}

	private CacheEntry minMove(MyState state) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		CacheEntry minEntry = null;
		for (Entry<Move, List<MachineState>> minMove : machine.getNextStates(
				state.getState(), minPlayer).entrySet()) {
			assert (minMove.getValue().size() == 1);
			MachineState nextMachineState = minMove.getValue().get(0);
			MyState nextState = new MyState(nextMachineState,
					state.getTurnNumber() + 1, maxPlayer);
			CacheEntry nextEntry = minmaxValueOf(nextState);
			if ((minEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() < minEntry
							.getValue()))) {
				minEntry = new CacheEntry(nextEntry.getValue(),
						minMove.getKey(), nextEntry.getHeight() + 1);
			}
		}
		return minEntry;
	}

}
