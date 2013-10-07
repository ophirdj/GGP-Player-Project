package interfaces;

import java.util.Collection;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.LabeledState;
import state.MyState;

/**
 * Interface for running game simulations.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface ISimulator extends Subject {

	/**
	 * Run simulation.
	 * 
	 * @param state
	 *            Starting state.
	 * @throws TransitionDefinitionException
	 * @throws MoveDefinitionException
	 * @throws GoalDefinitionException
	 */
	void Simulate(MyState state) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException;

	/**
	 * 
	 * @return A set of all state contents that were encountered during
	 *         simulations.
	 */
	Set<GdlSentence> getAllContents();

	/**
	 * 
	 * @return A collection of labeled states that were found during
	 *         simulations.
	 */
	Collection<LabeledState> getLabeledStates();

	class SimulatorEvent extends Event {

	}

	class SimulatorContents extends SimulatorEvent {

		private final Set<GdlSentence> contents;

		public SimulatorContents(Set<GdlSentence> contents) {
			this.contents = contents;
		}

		public Set<GdlSentence> getContents() {
			return contents;
		}

	}
	
	class SimulatorLabel extends SimulatorEvent {
		
		private final LabeledState label;

		public SimulatorLabel(LabeledState label) {
			this.label = label;
		}

		public LabeledState getLabel() {
			return label;
		}
	}

}
