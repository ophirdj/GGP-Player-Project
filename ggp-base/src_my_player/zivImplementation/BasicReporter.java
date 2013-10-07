package zivImplementation;

import java.util.ArrayList;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.observer.Subject;

public abstract class BasicReporter implements Subject {

	protected ArrayList<Observer> observers;
	
	public BasicReporter() {
		this.observers = new ArrayList<Observer>();
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
