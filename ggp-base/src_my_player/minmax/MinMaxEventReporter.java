package minmax;

import java.util.ArrayList;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;

public class MinMaxEventReporter implements Subject {

	private int exploredNodes;
	private int expandedNodes;
	private int sumBranchingFactor;

	private ArrayList<Observer> observers;

	public MinMaxEventReporter() {
		this.observers = new ArrayList<Observer>();
	}

	public void resetCount() {
		exploredNodes = 0;
		expandedNodes = 0;
		sumBranchingFactor = 0;
	}
	
	public void exploreNode() {
		++exploredNodes;
	}
	
	public void expandNode(int numChildren) {
		++expandedNodes;
		sumBranchingFactor += numChildren;
	}

	public void reportAndReset(Move selectedMove, int nodesInCache, int searchDepth, long duration) {
		notifyObservers(new MinMax.MinMaxEvent(selectedMove, exploredNodes, expandedNodes,
				nodesInCache, searchDepth, sumBranchingFactor / expandedNodes, duration));
		resetCount();
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		for (Observer observer : observers) {
			observer.observe(event);
		}
	}

}
