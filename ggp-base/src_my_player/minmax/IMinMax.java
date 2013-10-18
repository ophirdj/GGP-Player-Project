package minmax;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;

/**
 * Interface for generic minmax algorithm.
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
	 * @throws MoveDefinitionException
	 *             If could not generate move from state.
	 * @throws TransitionDefinitionException
	 *             If couldn't generate state's children.
	 * @throws GoalDefinitionException
	 *             If goal value wasn't defined for a terminal state.
	 * @throws InterruptedException
	 *             If interrupted during computation.
	 */
	Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, InterruptedException;

	/**
	 * Set the maximal search depth of minmax.
	 * 
	 * @param depth
	 *            Search depth.
	 */
	void setDepth(int depth);

	/**
	 * Set the time by which the minmax should return.
	 * 
	 * @param timeout
	 *            Computation deadline.
	 */
	void setTimeout(long timeout);

	/**
	 * Clear minmax meta-data.
	 */
	void clear();

	/**
	 * Generic exception for min-max.
	 * 
	 * @author ronen
	 * 
	 */
	class MinMaxException extends Exception {

		public MinMaxException() {
		}

		public MinMaxException(String string) {
			super(string);
		}

		private static final long serialVersionUID = -4734171837739535684L;

	}

	/**
	 * Event generated by minmax.
	 * 
	 * @author Ophir De Jager
	 *
	 */
	class MinMaxEvent extends Event {

		public final Move move;
		public final int exploredNodes;
		public final int expandedNodes;
		public final int prunedNodes;
		public final int terminalNodes;
		public final int nodesInCache;
		public final int cacheHits;
		public final int searchDepth;
		public final double averageBranchingFactor;
		public final long duration;

		public MinMaxEvent(Move selectedMove, int exploredNodes,
				int expandedNodes, int prunedNodes, int terminalNodes,
				int nodesInCache, int cacheHits, int searchDepth,
				double averageBranchingFactor, long duration) {
			this.move = selectedMove;
			this.exploredNodes = exploredNodes;
			this.expandedNodes = expandedNodes;
			this.prunedNodes = prunedNodes;
			this.terminalNodes = terminalNodes;
			this.nodesInCache = nodesInCache;
			this.cacheHits = cacheHits;
			this.searchDepth = searchDepth;
			this.averageBranchingFactor = averageBranchingFactor;
			this.duration = duration;
		}
	}

}
