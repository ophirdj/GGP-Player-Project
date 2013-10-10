package minmax;


import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;


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
	
	class MinMaxEvent extends Event {

		private final Move move;
		private final int exploredNodes;
		private final int expandedNodes;
		private final int prunedNodes;
		private final int terminalNodes;
		private final int nodesInCache;
		private final int cacheHits;
		private final int searchDepth;
		private final double averageBranchingFactor;
		private final long duration;

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

		public Move getMove() {
			return move;
		}

		public int getExploredNodes() {
			return exploredNodes;
		}

		public int getExpandedNodes() {
			return expandedNodes;
		}

		public int getPrunedNodes() {
			return prunedNodes;
		}

		public int getTerminalNodes() {
			return terminalNodes;
		}

		public int getNodesInCache() {
			return nodesInCache;
		}

		public int getCacheHits() {
			return cacheHits;
		}

		public int getSearchDepth() {
			return searchDepth;
		}

		public double getAverageBranchingFactor() {
			return averageBranchingFactor;
		}

		public long getDuration() {
			return duration;
		}

	}

}
