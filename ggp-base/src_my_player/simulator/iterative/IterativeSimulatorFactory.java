package simulator.iterative;

import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import simulator.ISimulator;
import simulator.ISimulatorFactory;

public class IterativeSimulatorFactory implements ISimulatorFactory {

	@Override
	public ISimulator createSimulator(StateMachine machine,
			IStateLabeler labeler, Role maxplayer) {
		return new IterativeSimulator(machine, labeler, maxplayer);
	}

	@Override
	public String toString() {
		return IterativeSimulator.class.getSimpleName();
	}
}
