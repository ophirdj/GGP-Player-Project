package minmax.anytime.alphabeta;

import java.util.List;
import java.util.Map.Entry;

import minmax.MinMaxCache;
import minmax.MinMaxCache.CacheEntry;
import minmax.MinMaxInfrastructure;

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

public final class AnyTimeAlphaBeta extends MinMaxInfrastructure {

	private static class TimeoutException extends Exception {

		private static final long serialVersionUID = -9155080794938185635L;
	}

	private MinMaxCache<MinMaxEntry> cache;

	public AnyTimeAlphaBeta(StateMachine machine, Role maxPlayer,
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
		Verbose.printVerbose("AnyTimeAlphaBeta: getMove start",
				Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move = null;
		try {
			for (int depth = 1; depth < getDepth(); ++depth) {
				Verbose.printVerbose("AnyTimeAlphaBeta: minmax to depth "
						+ depth, Verbose.MIN_MAX_VERBOSE);
				cache.prune();
				MinMaxEntry result = alphabeta(state, depth, null, null);
				move = result.move;
				long endTime = System.currentTimeMillis();
				reporter.reportAndReset(move, cache.size(), depth, endTime
						- startTime);
				if (result.noHeuristic) {
					break;
				}
			}
			Verbose.printVerbose("AnyTimeAlphaBeta: getMove end",
					Verbose.MIN_MAX_VERBOSE);
			return move;
		} catch (ClassificationException e) {
			Verbose.printVerboseError(
					"AnyTimeAlphaBeta: getMove exiting due to classification error",
					Verbose.MIN_MAX_VERBOSE);
			e.printStackTrace();
			throw new MinMaxException();
		} catch (TimeoutException e) {
			return move;
		} finally {
			reporter.resetCount();
		}
	}

	private MinMaxEntry alphabeta(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws ClassificationException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			MinMaxException, TimeoutException {
		if (System.currentTimeMillis() >= getTimeout()) {
			throw new TimeoutException();
		}
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
			minmaxEntry = new MinMaxEntry(goalValue, null, true);
			CacheEntry<MinMaxEntry> e = new CacheEntry<MinMaxEntry>(
					minmaxEntry, CacheEntry.TERMINAL_STATE_DEPTH);
			cache.put(state, e);
		} else if (depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(getValue(state), null, false);
		} else if (maxPlayer.equals(state.getRole())) {
			Verbose.printVerbose("MAX PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth, alpha, beta);
		} else if (minPlayer.equals(state.getRole())) {
			Verbose.printVerbose("MIN PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth, alpha, beta);
		} else {
			throw new MinMaxException(
					"minmax error: no match for controlingPlayer");
		}
		return minmaxEntry;
	}

	private MinMaxEntry executeMove(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, ClassificationException, MinMaxException,
			TimeoutException {
		MinMaxEntry bestEntry = null;
		List<Entry<Move, MyState>> children = expand(state);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> move : children) {
			++nodesVisited;
			MyState nextState = move.getValue();
			MinMaxEntry nextEntry = alphabeta(nextState, depth - 1, alpha, beta);

			if (bestEntry == null){
				bestEntry = new MinMaxEntry(nextEntry.value, move.getKey(),
						nextEntry.noHeuristic);
			}else if(isBetterThan(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry(nextEntry.value, move.getKey(),
						nextEntry.noHeuristic && bestEntry.noHeuristic);
			}

			if (state.getRole().equals(maxPlayer)) {
				alpha = bestEntry.value;
			} else {
				beta = bestEntry.value;
			}

			if (alpha != null && beta != null
					&& !isBetterThan(beta, alpha, maxPlayer)) {
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		cache.put(state, new CacheEntry<MinMaxEntry>(bestEntry, depth));
		return bestEntry;
	}
}
