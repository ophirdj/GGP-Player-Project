package interfaces;

import state.LabeledState;
import state.MyState;

/**
 * Interface for class to create the labels of game states.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IStateLabeler {

	/**
	 * Label a state.
	 * 
	 * @param state
	 *            State to be labeled.
	 * @return Corresponding labeled state.
	 */
	LabeledState label(MyState state);

	/**
	 * Get value of state.
	 * 
	 * @param state
	 *            State to be evaluated.
	 * @return State's (goal) value.
	 */
	double getValue(MyState state);

}
