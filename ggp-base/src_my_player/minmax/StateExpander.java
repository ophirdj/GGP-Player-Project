package minmax;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import classifier.IClassifier;

import states.MyState;

public class StateExpander {

	public static List<Entry<Move, MyState>> expand(StateMachine machine, MyState state, IClassifier classifier, Role maxPlayer) throws MoveDefinitionException, TransitionDefinitionException {
		Map<Move, List<MachineState>> nextStates = machine.getNextStates(state.getState(), state.getRole());
		List<Entry<Move, MyState>> childrenStates = new ArrayList<>(nextStates.size());
		for (Entry<Move, List<MachineState>> nextState : nextStates.entrySet()){
			assert(nextState.getValue().size() == 1);
			MyState childState = MyState.createChild(state, nextState.getValue().get(0));
			childrenStates.add(new SimpleEntry<Move, MyState>(nextState.getKey(), childState) );
		}
		sortMoves(childrenStates, classifier, maxPlayer);
		return childrenStates;
	}

	private static void sortMoves(List<Entry<Move, MyState>> childrenStates,
			IClassifier classifier, Role maxPlayer) {
		
	}
}
