package minmax.limiteddepth;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import classifier.IClassifier.ClassificationException;
import classifier.IClassifier.ClassifierValue;
import states.MyState;

public class StateExpander {

	public static List<Entry<Move, MyState>> expand(StateMachine machine,
			MyState state, IClassifier classifier, Role maxPlayer)
			throws MoveDefinitionException, TransitionDefinitionException,
			ClassificationException {
		Map<Move, List<MachineState>> nextStates = machine.getNextStates(
				state.getState(), state.getRole());
		List<Entry<Move, MyState>> childrenStates = new ArrayList<Entry<Move, MyState>>(
				nextStates.size());
		for (Entry<Move, List<MachineState>> nextState : nextStates.entrySet()) {
			assert (nextState.getValue().size() == 1);
			MyState childState = MyState.createChild(state, nextState
					.getValue().get(0));
			childrenStates.add(new SimpleEntry<Move, MyState>(nextState
					.getKey(), childState));
		}
		sortMoves(childrenStates, classifier, !state.getRole()
				.equals(maxPlayer));
		return childrenStates;
	}

	private static class Comparer implements Comparator<Entry<Move, MyState>> {

		private IClassifier classifier;
		private boolean reverse;

		public Comparer(IClassifier classifier, boolean reverse) {
			this.classifier = classifier;
			this.reverse = reverse;
		}

		public boolean isBetter(MyState state1, MyState state2)
				throws ClassificationException {
			ClassifierValue value1 = classifier.getValue(state1);
			ClassifierValue value2 = classifier.getValue(state2);
			boolean better = classifier.isBetterValue(value1, value2);
			return better ^ reverse;
		}

		@Override
		public int compare(Entry<Move, MyState> e1, Entry<Move, MyState> e2) {
			try {
				if(isBetter(e1.getValue(), e2.getValue())) {
					return 1;
				} else if (isBetter(e2.getValue(), e1.getValue())) {
					return -1;
				} else {
					return 0;
				}
			} catch (ClassificationException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	private static void sortMoves(List<Entry<Move, MyState>> childrenStates,
			IClassifier classifier, boolean reverse)
			throws ClassificationException {
		Collections.sort(childrenStates, new Comparer(classifier, reverse));
	}
}
