package simulator;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;


public interface MapValueSimulatorFactory {
	
	MapValueSimulator createSimulator(StateMachine machine, Role myRole, Role oponentRole);
	
	
	//TODO: add events for observer to observe
	
}
