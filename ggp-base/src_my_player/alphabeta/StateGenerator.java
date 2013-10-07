package alphabeta;
import heuristics.StateClassifier.ClassificationException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;


public class StateGenerator {
	
	public static interface CompareFunction {
		boolean isGreater(MyState state1, MyState state2) throws ClassificationException, GoalDefinitionException;
	}
	
	private StateMachine machine;

	public StateGenerator(StateMachine machine) {
		this.machine = machine;
	}
	
	
	public List<Entry<Move, MyState>> getNextStates(MyState fatherState,
			Role player, Role nextPlayer, CompareFunction sortFunction)
			throws MoveDefinitionException, TransitionDefinitionException, ClassificationException, GoalDefinitionException {
		Map<Move, List<MachineState>> nextStates = machine.getNextStates(
				fatherState.getState(), player);
		ArrayList<Entry<Move, MyState>> childrenStates = new ArrayList<Entry<Move, MyState>>(
				nextStates.size());
		for (Entry<Move, List<MachineState>> child : nextStates.entrySet()) {
			MyState childState = MyState.createChild(fatherState, child.getValue().get(0));
			childrenStates.add(new AbstractMap.SimpleEntry<Move, MyState>(child
					.getKey(), childState));
		}
		sortMoves(childrenStates, sortFunction);
		return childrenStates;
	}

	
	private void sortMoves(ArrayList<Entry<Move, MyState>> states,
			CompareFunction sortFunction) throws ClassificationException, GoalDefinitionException {
		quicksort(states, 0, states.size() - 1, sortFunction);
	}

	private void quicksort(ArrayList<Entry<Move, MyState>> states, int low,
			int high, CompareFunction sortFunction) throws ClassificationException, GoalDefinitionException {
		int i = low, j = high;
		MyState pivot = states.get(low + (high - low) / 2).getValue();
		while (i <= j) {
			while (i <= high && sortFunction.isGreater(pivot, states.get(i).getValue())) {
				++i;
			}
			while (j >= low && sortFunction.isGreater(states.get(j).getValue(), pivot)) {
				--j;
			}
			if (i < j) {
				Entry<Move, MyState> tmp = states.get(i);
				states.set(i, states.get(j));
				states.set(j, tmp);
			}
		}
		if (low < j) {
			quicksort(states, low, j, sortFunction);
		}
		if (i < high) {
			quicksort(states, low, i, sortFunction);
		}
	}

}
