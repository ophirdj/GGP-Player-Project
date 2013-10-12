package labeler;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

public interface IStateLabelerFactory {

	/**
	 * 
	 * @param machine A state machine.
	 * @param player A role.
	 * @return A new IStateLabeler.
	 */
	IStateLabeler createStateLabeler(StateMachine machine, Role player);
	
}
