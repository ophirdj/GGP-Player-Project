package minmax.limiteddepth.alphabeta;

import java.util.List;
import java.util.Map.Entry;

import minmax.limiteddepth.LimitedDepthMinMax;

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

public final class AlphaBeta extends LimitedDepthMinMax {

	private boolean execute = false;

	public AlphaBeta(StateMachine machine, Role maxPlayer,
			IClassifier classifier, int depth, boolean cached) {
		super(machine, maxPlayer, classifier, depth, cached);
	}

	@Override
	public Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, InterruptedException {
		execute = true;
		if (state == null) {
			throw new MinMaxException();
		}
		Verbose.printVerbose("AlphaBeta: start", Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move;
		try {
			move = alphabeta(state, minMaxDepth, null, null).move;
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, 0, minMaxDepth, endTime - startTime);
			return move;
		} catch (ClassificationException e) {
			e.printStackTrace();
			Verbose.printVerboseError("Classification fail",
					Verbose.MIN_MAX_VERBOSE);
			throw new MinMaxException();
		} catch (InterruptedException e) {
			reporter.resetCount();
			throw e;
		}
	}

	private MinMaxEntry alphabeta(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws ClassificationException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			MinMaxException, InterruptedException {
		if (isTimeout() || !execute) {
			throw new InterruptedException();
		}
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = searchCache(state, depth);
		if (minmaxEntry != null) {
			return minmaxEntry;
		} else if (isTerminal(state)) {
			reporter.visitTerminal();
			ClassifierValue goalValue = getValue(state);
			Verbose.printVerbose("AlphaBeta: final state with goal value "
					+ goalValue, Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(goalValue, null, true);
			addToCache(state, minmaxEntry, depth);
		} else if (depth <= 0) {
			Verbose.printVerbose("AlphaBeta: final depth reached",
					Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(getValue(state), null, false);
			addToCache(state, minmaxEntry, depth);
		} else if (maxPlayer.equals(state.getRole())) {
			Verbose.printVerbose("AlphaBeta: max move", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth, alpha, beta);
		} else if (minPlayer.equals(state.getRole())) {
			Verbose.printVerbose("AlphaBeta: min move", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = executeMove(state, depth, alpha, beta);
		} else {
			throw new MinMaxException("no match for controlingPlayer");
		}
		return minmaxEntry;
	}

	private MinMaxEntry executeMove(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, ClassificationException, MinMaxException,
			InterruptedException {
		MinMaxEntry bestEntry = null;
		List<Entry<Move, MyState>> children = expand(state);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> move : children) {
			++nodesVisited;
			MyState nextState = move.getValue();
			MinMaxEntry nextEntry = alphabeta(nextState, depth - 1, alpha, beta);

			if (bestEntry == null) {
				bestEntry = new MinMaxEntry(nextEntry.value, move.getKey(),
						nextEntry.noHeuristic);
			} else if (isBetterThan(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry(nextEntry.value, move.getKey(),
						nextEntry.noHeuristic);
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
		addToCache(state, bestEntry, depth);
		return bestEntry;
	}

	@Override
	public void stop() {
		execute  = false;
	}
}
