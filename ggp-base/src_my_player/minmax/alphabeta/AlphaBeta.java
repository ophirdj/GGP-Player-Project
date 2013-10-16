package minmax.alphabeta;

import java.util.List;
import java.util.Map.Entry;

import minmax.limiteddepth.LimitedDepthMinMaxInfrastructure;

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

/**
 * A simple implementation of the min-max algorithm. No cache or alpha-beta.
 * 
 * @author ronen
 * 
 */
public class AlphaBeta extends LimitedDepthMinMaxInfrastructure {

	public AlphaBeta(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		super(machine, maxPlayer, classifier);
	}

	@Override
	public void clear() {
		// do nothing
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
			move = alphabeta(state, getDepth(), null, null).getMove();
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, 0, getDepth(), endTime - startTime);
			return move;
		} catch (ClassificationException e) {
			e.printStackTrace();
			Verbose.printVerboseError("Classification fail",
					Verbose.MIN_MAX_VERBOSE);
			throw new MinMaxException();
		}
	}

	protected MinMaxEntry alphabeta(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws ClassificationException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			MinMaxException {
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = null;
		if (isTerminal(state)) {
			reporter.visitTerminal();
			ClassifierValue goalValue = getValue(state);
			Verbose.printVerbose("Final State with goal value " + goalValue,
					Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(goalValue, null);
		} else if (depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(getValue(state), null);
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

	protected MinMaxEntry executeMove(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, ClassificationException, MinMaxException {
		MinMaxEntry bestEntry = null;
		List<Entry<Move, MyState>> children = expand(state);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> move : children) {
			++nodesVisited;
			MyState nextState = move.getValue();
			MinMaxEntry nextEntry = alphabeta(nextState, depth - 1, alpha, beta);

			if (bestEntry == null) {
				bestEntry = new MinMaxEntry(nextEntry.getValue(), move.getKey());
			} else if (isBetterThan(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry(nextEntry.getValue(), move.getKey());
			}

			if (state.getRole().equals(maxPlayer)) {
				alpha = bestEntry.getValue();
			} else {
				beta = bestEntry.getValue();
			}

			if (alpha != null && beta != null
					&& !isBetterThan(beta, alpha, maxPlayer)) {
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		return bestEntry;
	}
}
