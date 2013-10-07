package interfaces;

import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;

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
	 */
	Move getMove(MyState state);

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

}
