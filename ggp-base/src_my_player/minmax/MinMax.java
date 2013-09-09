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

	private static final int CACHE_HEIGHT_THRESHOLD = 2;
	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private Map<MachineState, CacheEntry> cache;

	
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
		
		public Move getMove(){
			return bestMove;
		}
		
		public int getHeight(){
			return height;
		}
	}

	
	public MinMax(StateMachine machine, Role maxPlayer) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles.get(0));
		this.cache = new HashMap<MachineState, CacheEntry>();
	}

	public void pruneCache() {
		Set<MachineState> toBeDeleted = new HashSet<MachineState>();
		for (Entry<MachineState, CacheEntry> entry : cache.entrySet()) {
			if (entry.getValue() != null && entry.getValue().canPrune()) {
				toBeDeleted.add(entry.getKey());
			} else if (entry.getValue() != null) {
				entry.getValue().reduceTTL();
			}
		}
		for (MachineState key : toBeDeleted) {
			cache.remove(key);
		}
	}

	public Integer valuOf(MachineState state, Role controlingPlayer)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException {
		if (state == null || controlingPlayer == null) {
			return null;
		}
		return minmaxValueOf(state, controlingPlayer).getValue();
	}

	private CacheEntry minmaxValueOf(MachineState state, Role controlingPlayer)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException {
		if (cache.containsKey(state) && cache.get(state) != null) {
			return cache.get(state);
		} else if (cache.containsKey(state)) {
			return null;
		}
		CacheEntry minmaxEntry = null;
		cache.put(state, null);
		if (machine.isTerminal(state)) {
			return new CacheEntry(machine.getGoal(state, maxPlayer) - machine.getGoal(state, minPlayer), null, 0);
		} else if (controlingPlayer == maxPlayer) {
			minmaxEntry = maxMove(state);
		} else if (controlingPlayer == minPlayer) {
			minmaxEntry = minMove(state);
		} else {
			throw new RuntimeException(
					"minmax error: no match for controlingPlayer");
		}
		if(minmaxEntry.getHeight() >= CACHE_HEIGHT_THRESHOLD){
			cache.put(state, minmaxEntry);
		}
		return minmaxEntry;
	}

	private CacheEntry maxMove(MachineState state) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		CacheEntry maxEntry = null;
		for(Entry<Move, List<MachineState>> maxMove: machine.getNextStates(state, maxPlayer).entrySet()){
			assert(maxMove.getValue().size() == 1);
			MachineState nextState = maxMove.getValue().get(0);
			CacheEntry nextEntry = minmaxValueOf(nextState, minPlayer);
			if ((maxEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() > maxEntry.getValue()))) {
				maxEntry = new CacheEntry(nextEntry.getValue(), maxMove.getKey(), nextEntry.getHeight());
			}
		}
		return maxEntry;
	}

	private CacheEntry minMove(MachineState state) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		CacheEntry minEntry = null;
		for(Entry<Move, List<MachineState>> minMove: machine.getNextStates(state, minPlayer).entrySet()){
			assert(minMove.getValue().size() == 1);
			MachineState nextState = minMove.getValue().get(0);
			CacheEntry nextEntry = minmaxValueOf(nextState, maxPlayer);
			if ((minEntry == null)
					|| ((nextEntry != null) && (nextEntry.getValue() < minEntry.getValue()))) {
				minEntry = new CacheEntry(nextEntry.getValue(), minMove.getKey(), nextEntry.getHeight());
			}
		}
		return minEntry;
	}

}
