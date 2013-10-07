package minmax;

import heuristics.StateClassifier.ClassificationException;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

public interface MinMax extends Subject {

	Move bestMove(MyState state) throws MinMaxException,
			GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException, ClassificationException;

	void clear();

	class MinMaxException extends Exception {
		private static final long serialVersionUID = 2089399546677381050L;

		public MinMaxException() {
			super();
		}

		public MinMaxException(String message) {
			super(message);
		}

	};

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
