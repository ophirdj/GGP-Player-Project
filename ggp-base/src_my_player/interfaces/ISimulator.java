package interfaces;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.observer.Subject;

import state.LabeledState;

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
	 */
	void Simulate();

	/**
	 * 
	 * @return A set of all state contents that were encountered during
	 *         simulations.
	 */
	Set<GdlSentence> getAllContents();

	/**
	 * 
	 * @return A set of labeled states that were found during simulations.
	 */
	Set<LabeledState> getLabeledStates();

}
