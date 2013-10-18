package minmax.limiteddepth;


import minmax.IMinMax.MinMaxEvent;

import org.ggp.base.util.statemachine.Move;

import utils.BasicReporter;


public class MinMaxReporter extends BasicReporter {
	
	private int exploredNodes;
	private int expandedNodes;
	private int prunedNodes;
	private int terminalNodes;
	private int cacheHits;
	private int sumBranchingFactor;

	public void resetCount() {
		exploredNodes = 0;
		expandedNodes = 0;
		prunedNodes = 0;
		terminalNodes = 0;
		cacheHits = 0;
		sumBranchingFactor = 0;
	}
	
	public void exploreNode() {
		++exploredNodes;
	}
	
	public void expandNode(int numChildren) {
		++expandedNodes;
		sumBranchingFactor += numChildren;
	}
	
	public void prune(int numPruned) {
		prunedNodes += numPruned;
	}
	
	public void visitTerminal() {
		++terminalNodes;
	}
	
	public void cacheHit() {
		++cacheHits;
	}

	public void reportAndReset(Move selectedMove, int nodesInCache, int searchDepth, long duration) {
		notifyObservers(new MinMaxEvent(selectedMove, exploredNodes, expandedNodes, prunedNodes, terminalNodes,
				nodesInCache, cacheHits, searchDepth, sumBranchingFactor / (double) expandedNodes, duration));
		resetCount();
	}
	
}
