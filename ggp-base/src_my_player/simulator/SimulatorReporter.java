package simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.LabeledState;
import utils.BasicReporter;
import simulator.ISimulator.SimulatorEvent;;

public final class SimulatorReporter extends BasicReporter {

	private Map<Double, Integer> labelsHistogram = new HashMap<Double, Integer>();
	private int discoveredStates;
	private int labeledStates;
	

	public void resetCount() {
		labelsHistogram.clear();
		discoveredStates =  0;
		labeledStates = 0;
	}
	
	public void discoverState() {
		++discoveredStates;
	}
	
	public void labeledState(LabeledState labeledState) {
		++labeledStates;
		double label = labeledState.getValue();
		if(labelsHistogram.containsKey(label)) {
			labelsHistogram.put(label, labelsHistogram.get(label) + 1);
		} else {
			labelsHistogram.put(label, 1);
		}
	}
	
	public void reportAndReset(Set<GdlSentence> contents) {
		notifyObservers(new SimulatorEvent(contents, labelsHistogram, discoveredStates, labeledStates));
		resetCount();
	}
	
}
