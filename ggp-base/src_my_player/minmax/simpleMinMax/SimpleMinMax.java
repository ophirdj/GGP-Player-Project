package minmax.simpleMinMax;


import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import minmax.MinMaxInfrastructure;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import classifier.IClassifier;
import classifier.IClassifier.ClassificationException;
import classifier.IClassifier.ClassifierValue;

import states.MyState;
import utils.Verbose;


/**
 * A simple implementation of the min-max algorithm. 
 * No cache or alpha-beta. 
 * 
 * @author ronen
 *
 */
public class SimpleMinMax extends MinMaxInfrastructure {
	
	private StateMachine machine;
	private Role maxPlayer;
	private IClassifier classifier;
	private Role minPlayer;
	private List<Role> roles;

	public SimpleMinMax(StateMachine machine, Role maxPlayer, IClassifier classifier) {
		this.roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = nextRole(maxPlayer);
		this.classifier = classifier;
	}

	/**
	 * Return the role of the other player.
	 * @param role The current role.
	 * @return The next (other) role.
	 */
	private Role nextRole(Role role) {
		return roles.get(0).equals(role) ? roles.get(1) : roles
				.get(0);
	}
	
	@Override
	public Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		if (state == null) {
			throw new MinMaxException();
		}
		Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move;
		try {
			move = valueOf(state, getDepth()).getMove();
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, 0, getDepth(), endTime - startTime);
			return move;
		} catch (ClassificationException e) {
			e.printStackTrace();
			Verbose.printVerboseError("Classification fail", Verbose.MIN_MAX_VERBOSE);
			throw new MinMaxException();
		}
	}
	
	private MinMaxEntry valueOf(MyState state, int depth) throws ClassificationException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException, MinMaxException{
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = null;
		if (machine.isTerminal(state.getState())) {
			reporter.visitTerminal();
			ClassifierValue goalValue = classifier.getValue(state);
			Verbose.printVerbose("Final State with goal value " + goalValue, Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(goalValue, null);
		} else if(depth < 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			return new MinMaxEntry(classifier.getValue(state), null);
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

	private MinMaxEntry executeMove(MyState state, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException, ClassificationException, MinMaxException {
		MinMaxEntry bestEntry = null;
		Set<Entry<Move, List<MachineState>>> children = machine.getNextStates(
				state.getState(), state.getRole()).entrySet();
		reporter.expandNode(children.size());
		for (Entry<Move, List<MachineState>> move : children) {
			assert (move.getValue().size() == 1);
			MachineState nextMachineState = move.getValue().get(0);
			MyState nextState = MyState.createChild(state, nextMachineState);
			MinMaxEntry nextEntry = valueOf(nextState, depth - 1);
			if(bestEntry == null){
				bestEntry = new MinMaxEntry(nextEntry.getValue(), move.getKey());
			}
			else if(isBetterThen(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry (nextEntry.getValue(), move.getKey());
			}
		}
		return bestEntry;
	}
	
	/*
	 * Return true if (and only if) nextEntry is better then bestEntry.
	 * Meaning, only return true if the player will want to switch to nextEntry.
	 */
	private boolean isBetterThen(MinMaxEntry nextEntry, MinMaxEntry bestEntry,
			Role controlingPlayer) throws ClassificationException {
		if(controlingPlayer.equals(maxPlayer)){
			return classifier.isBetterValue(nextEntry.getValue(), bestEntry.getValue());
		}
		//Controling player is min player.
		else{
			return classifier.isBetterValue(bestEntry.getValue(), nextEntry.getValue());
		}
	}


	public static class MinMaxEntry{
		private final ClassifierValue value;
		private final Move bestMove;

		public MinMaxEntry(ClassifierValue value, Move bestMove) {
			Verbose.printVerbose("State value is " + value, Verbose.MIN_MAX_VERBOSE);
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
