package simulator;

import java.util.HashSet;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.statemachine.StateMachine;

import states.MyState;

public abstract class BaseSimulator implements ISimulator {

	protected final StateMachine machine;
	protected final IStateLabeler labeler;
	private Set<GdlSentence> contents;
	protected SimulatorReporter reporter;

	public BaseSimulator(StateMachine machine, IStateLabeler labeler) {
		this.machine = machine;
		this.labeler = labeler;
		this.reporter = new SimulatorReporter();
		this.contents = new HashSet<GdlSentence>();
	}

	@Override
	public final void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public final void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

	protected final void addContents(MyState state) {
		contents.addAll(state.getContents());
	}

	@Override
	public final Set<GdlSentence> getAllContents() {
		return contents;
	}

}
