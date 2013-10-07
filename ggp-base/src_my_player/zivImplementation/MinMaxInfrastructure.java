package zivImplementation;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;

import interfaces.IMinMax;

public abstract class MinMaxInfrastructure implements IMinMax {

	private static final int DEFAULT_MINMAX_DEPTH = 2;
	
	protected MinMaxReporter reporter;
	private int minMaxDepth;
	
	public MinMaxInfrastructure() {
		this.reporter = new MinMaxReporter();
		minMaxDepth = DEFAULT_MINMAX_DEPTH;
	}
	
	@Override
	public void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

	public synchronized int getDepth() {
		return minMaxDepth;
	}

	@Override
	public synchronized void setDepth(int minMaxDepth) {
		this.minMaxDepth = minMaxDepth;
	}

}
