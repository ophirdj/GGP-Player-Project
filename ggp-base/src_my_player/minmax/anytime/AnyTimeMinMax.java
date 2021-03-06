package minmax.anytime;

import minmax.IMinMax;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;
import utils.Verbose;

public final class AnyTimeMinMax implements IMinMax {

	public static final int DEFAULT_MINMAX_MAX_DEPTH = 10;

	private long timeout;
	private final IMinMax minmax;
	private AnyTimeWorker worker;

	private boolean execute = false;

	public AnyTimeMinMax(IMinMax minmax) {
		this.timeout = Long.MIN_VALUE;
		this.minmax = minmax;
		this.worker = new AnyTimeWorker(minmax);
		worker.start();
	}

	@Override
	public void addObserver(Observer observer) {
		minmax.addObserver(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		minmax.notifyObservers(event);
	}

	@Override
	public Move getMove(MyState state) throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException, InterruptedException {
		if (System.currentTimeMillis() < timeout) {
			Verbose.printVerbose("AnyTimeMinMax: starting worker",
					Verbose.MIN_MAX_VERBOSE);
			worker.startMinMaxLoop(state, timeout);
			try {
				Verbose.printVerbose("AnyTimeMinMax: going to sleep... zZZ",
						Verbose.MIN_MAX_VERBOSE);
				Thread.sleep(timeout - System.currentTimeMillis());
				Verbose.printVerbose("AnyTimeMinMax: good morning!",
						Verbose.MIN_MAX_VERBOSE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Verbose.printVerbose("AnyTimeMinMax: stoping worker",
					Verbose.MIN_MAX_VERBOSE);
			if(execute) worker.stopMinMaxLoop();
			Verbose.printVerbose("AnyTimeMinMax: getting move result",
					Verbose.MIN_MAX_VERBOSE);
			return worker.getMove();
		}
		return null;
	}

	@Override
	public void setDepth(int depth) {
		worker.setMaxDepth(depth);
	}

	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public void clear() {
		minmax.clear();
	}

	@Override
	public void stop() {
		execute  = false;
		worker.stopMinMaxLoop();
	}

}
