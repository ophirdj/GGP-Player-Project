package simulator.knownvalue;



import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import simulator.ISimulator;
import simulator.ISimulatorFactory;
import states.IStateLabeler;


public class KnownValueSimulatorFactory implements ISimulatorFactory {

	@Override
	public ISimulator createSimulator(StateMachine machine,
			IStateLabeler labeler, Role maxplayer) {
		return new KnownValueSimulator(machine, labeler, maxplayer);
	}
	
	@Override
	public String toString() {
		return KnownValueSimulator.class.getSimpleName();
	}

}
