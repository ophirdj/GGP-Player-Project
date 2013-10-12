package minmax.simple.cached;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import minmax.MinMaxCache;
import minmax.MinMaxCache.CacheEntry;
import minmax.simple.SimpleMinMax;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;
import utils.Verbose;
import classifier.IClassifier;
import classifier.IClassifier.ClassificationException;
import classifier.IClassifier.ClassifierValue;

public class CachedSimpleMinMax extends SimpleMinMax {

	private MinMaxCache<MinMaxEntry> cache;

	public CachedSimpleMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		super(machine, maxPlayer, classifier);
		this.cache = new MinMaxCache<MinMaxEntry>();
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		if (state == null) {
			throw new MinMaxException();
		}
		Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move;
		try {
			cache.prune();
			move = minmax(state, getDepth()).getMove();
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, cache.size(), getDepth(), endTime - startTime);
			return move;
		} catch (ClassificationException e) {
			e.printStackTrace();
			Verbose.printVerboseError("Classification fail",
					Verbose.MIN_MAX_VERBOSE);
			throw new MinMaxException();
		}
	}

	@Override
	protected MinMaxEntry minmax(MyState state, int depth)
			throws ClassificationException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			MinMaxException {
		if (cache.contains(state, depth)) {
			reporter.cacheHit();
			return cache.get(state);
		}
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = null;
		if (isTerminal(state)) {
			reporter.visitTerminal();
			ClassifierValue goalValue = getValue(state);
			Verbose.printVerbose("Final State with goal value " + goalValue,
					Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(goalValue, null);
			CacheEntry<MinMaxEntry> e = new CacheEntry<MinMaxEntry>(
					minmaxEntry, CacheEntry.TERMINAL_STATE_DEPTH);
			cache.put(state, e);
		} else if (depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(getValue(state), null);
		} else if (maxPlayer.equals(state.getRole())) {
			Verbose.printVerbose("MAX PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth);
		} else if (minPlayer.equals(state.getRole())) {
			Verbose.printVerbose("MIN PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth);
		} else {
			throw new MinMaxException(
					"minmax error: no match for controlingPlayer");
		}
		return minmaxEntry;
	}

	@Override
	protected MinMaxEntry executeMove(MyState state, int depth)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, ClassificationException, MinMaxException {
		MinMaxEntry bestEntry = null;
		Set<Entry<Move, List<MachineState>>> children = machine.getNextStates(
				state.getState(), state.getRole()).entrySet();
		reporter.expandNode(children.size());
		for (Entry<Move, List<MachineState>> move : children) {
			assert (move.getValue().size() == 1);
			MachineState nextMachineState = move.getValue().get(0);
			MyState nextState = MyState.createChild(state, nextMachineState);
			MinMaxEntry nextEntry = minmax(nextState, depth - 1);
			if (bestEntry == null) {
				bestEntry = new MinMaxEntry(nextEntry.getValue(), move.getKey());
			} else if (isBetterThan(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry(nextEntry.getValue(), move.getKey());
			}
		}
		cache.put(state, new CacheEntry<MinMaxEntry>(bestEntry, depth));
		return bestEntry;
	}

}
