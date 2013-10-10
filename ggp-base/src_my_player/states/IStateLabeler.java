package states;

import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;


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
	 * @throws GoalDefinitionException 
	 */
	LabeledState label(MyState state) throws GoalDefinitionException;

	/**
	 * Create a label to the state with given value.
	 * 
	 * @param state
	 *            State to be labeled.
	 * @param value
	 *            Label value.
	 * @return Corresponding labeled state.
	 */
	LabeledState createLabel(MyState state, double value);

}
