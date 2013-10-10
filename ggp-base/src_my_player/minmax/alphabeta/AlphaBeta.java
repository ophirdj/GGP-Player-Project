package minmax.alphabeta;

import java.util.List;
import java.util.Map.Entry;

import minmax.MinMaxInfrastructure;
import minmax.StateExpander;

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
public class AlphaBeta extends MinMaxInfrastructure {

	private StateMachine machine;
	private Role maxPlayer;
	private IClassifier classifier;
	private Role minPlayer;
	private List<Role> roles;

	public AlphaBeta(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		this.roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = nextRole(maxPlayer);
		this.classifier = classifier;
	}

	/**
	 * Return the role of the other player.
	 * 
	 * @param role
	 *            The current role.
	 * @return The next (other) role.
	 */
	private Role nextRole(Role role) {
		return roles.get(0).equals(role) ? roles.get(1) : roles.get(0);
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

	private MinMaxEntry alphabeta(MyState state, int depth,
			ClassifierValue alpha, ClassifierValue beta)
			throws ClassificationException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			MinMaxException {
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = null;
		if (machine.isTerminal(state.getState())) {
			reporter.visitTerminal();
			ClassifierValue goalValue = classifier.getValue(state);
			Verbose.printVerbose("Final State with goal value " + goalValue,
					Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(goalValue, null);
		} else if (depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(classifier.getValue(state), null);
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
			GoalDefinitionException, ClassificationException, MinMaxException {
		MinMaxEntry bestEntry = null;
		List<Entry<Move, MyState>> children = StateExpander.expand(machine, state, classifier, maxPlayer);
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
			
			if (state.getRole().equals(maxPlayer)){
				alpha = bestEntry.getValue();
			}
			else{
				beta = bestEntry.getValue();
			}
			
			if (alpha != null && beta != null && !classifier.isBetterValue(beta, alpha)){
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		return bestEntry;
	}

	/*
	 * Return true if (and only if) nextEntry is better then bestEntry. Meaning,
	 * only return true if the player will want to switch to nextEntry.
	 */
	private boolean isBetterThan(MinMaxEntry nextEntry, MinMaxEntry bestEntry,
			Role controlingPlayer) throws ClassificationException {
		if (controlingPlayer.equals(maxPlayer)) {
			return classifier.isBetterValue(nextEntry.getValue(),
					bestEntry.getValue());
		}
		// Controling player is min player.
		else {
			return classifier.isBetterValue(bestEntry.getValue(),
					nextEntry.getValue());
		}
	}

	public static class MinMaxEntry {
		private final ClassifierValue value;
		private final Move bestMove;

		public MinMaxEntry(ClassifierValue value, Move bestMove) {
			Verbose.printVerbose("State value is " + value,
					Verbose.MIN_MAX_VERBOSE);
			this.value = value;
			this.bestMove = bestMove;
		}

		public ClassifierValue getValue() {
			return value;
		}

		public Move getMove() {
			return bestMove;
		}

	}

	@Override
	public void clear() {

	}

}
