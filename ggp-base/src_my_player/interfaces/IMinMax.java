package interfaces;

import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

/**
 * Interface for minmax/alpha-beta algorithm.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IMinMax extends Subject {

	/**
	 * Invoke minmax algorithm to determine the best move the player can do.
	 * 
	 * @param state
	 *            Current state.
	 * @return Best move player can do.
	 * @throws GoalDefinitionException 
	 * @throws TransitionDefinitionException 
	 * @throws MoveDefinitionException 
	 */
	Move getMove(MyState state) throws MinMaxException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException;

	/**
	 * Set the search depth of minmax.
	 * 
	 * @param depth
	 *            Search depth (should not be negative)
	 */
	void setDepth(int depth);

	/**
	 * Clear minmax meta-data.
	 */
	void clear();
	
	/**
	 * Generic exception for min-max.
	 * @author ronen
	 *
	 */
	class MinMaxException extends Exception{

		public MinMaxException(){}
		
		public MinMaxException(String string) {
			super(string);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -4734171837739535684L;
		
	}

}
