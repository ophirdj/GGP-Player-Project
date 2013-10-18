package simulator.base;

import java.util.Collection;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import simulator.ISimulator;
import simulator.ISimulator.SimulatorEvent;
import states.LabeledState;
import utils.BasicReporter;

public final class SimulatorReporter extends BasicReporter {

	private int discoveredStates;

	public void resetCount() {
		discoveredStates = 0;
	}

	public void discoverState() {
		++discoveredStates;
	}

	public void reportAndReset(Set<GdlSentence> contents,
			Collection<LabeledState> labeledStates) {
		notifyObservers(new SimulatorEvent(contents, labeledStates,
				discoveredStates));
		resetCount();
	}

}
