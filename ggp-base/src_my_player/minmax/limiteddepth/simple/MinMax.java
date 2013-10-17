package minmax.limiteddepth.simple;


import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import minmax.limiteddepth.LimitedDepthMinMax;

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


/**
 * A simple implementation of the min-max algorithm. 
 * No cache or alpha-beta. 
 * 
 * @author ronen
 *
 */
public final class MinMax extends LimitedDepthMinMax {

	public MinMax(StateMachine machine, Role maxPlayer, IClassifier classifier, int depth, boolean cached) {
		super(machine, maxPlayer, classifier, depth, cached);
	}
	
	@Override
	public Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException, InterruptedException {
		if (state == null) {
			throw new MinMaxException();
		}
		Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move;
		try {
			move = minmax(state, minMaxDepth).move;
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, 0, minMaxDepth, endTime - startTime);
			return move;
		} catch (ClassificationException e) {
			e.printStackTrace();
			Verbose.printVerboseError("Classification fail", Verbose.MIN_MAX_VERBOSE);
			throw new MinMaxException();
		}
	}
	
	private MinMaxEntry minmax(MyState state, int depth) throws ClassificationException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException, MinMaxException, InterruptedException{
		if(isTimeout()) {
			throw new InterruptedException();
		}
		reporter.exploreNode();
		MinMaxEntry minmaxEntry = searchCache(state, depth);
		if(minmaxEntry != null) {
			return minmaxEntry;
		} else if (isTerminal(state)) {
			reporter.visitTerminal();
			ClassifierValue goalValue = getValue(state);
			Verbose.printVerbose("Final State with goal value " + goalValue, Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(goalValue, null, true);
			addToCache(state, minmaxEntry, depth);
		} else if(depth <= 0) {
			Verbose.printVerbose("FINAL DEPTH REACHED", Verbose.MIN_MAX_VERBOSE);
			minmaxEntry = new MinMaxEntry(getValue(state), null, false);
			addToCache(state, minmaxEntry, depth);
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
			TransitionDefinitionException, GoalDefinitionException, ClassificationException, MinMaxException, InterruptedException {
		MinMaxEntry bestEntry = null;
		Set<Entry<Move, List<MachineState>>> children = machine.getNextStates(
				state.getState(), state.getRole()).entrySet();
		reporter.expandNode(children.size());
		for (Entry<Move, List<MachineState>> move : children) {
			assert (move.getValue().size() == 1);
			MachineState nextMachineState = move.getValue().get(0);
			MyState nextState = MyState.createChild(state, nextMachineState);
			MinMaxEntry nextEntry = minmax(nextState, depth - 1);
			if(bestEntry == null){
				bestEntry = new MinMaxEntry(nextEntry.value, move.getKey(), nextEntry.noHeuristic);
			}
			else if(isBetterThan(nextEntry, bestEntry, state.getRole())) {
				bestEntry = new MinMaxEntry (nextEntry.value, move.getKey(), nextEntry.noHeuristic);
			}
		}
		addToCache(state, bestEntry, depth);
		return bestEntry;
	}

}
