package simulator;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import interfaces.ISimulator;
import interfaces.ISimulatorFactory;
import interfaces.IStateLabeler;

public class SimpleSimulatorFactory implements ISimulatorFactory {

	@Override
	public ISimulator createSimulator(StateMachine machine,
			IStateLabeler labeler, Role maxplayer) {
		return new MySimpleSimulator(machine, labeler, maxplayer);
	}
	
	@Override
	public String toString() {
		return MySimpleSimulator.class.getSimpleName();
	}

}
