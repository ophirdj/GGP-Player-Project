package interfaces;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

public interface ISimulatorFactory {
	
	
	/**
	 * 
	 * @param machine The state machine of the game.
	 * @param labeler A Labeler that give terminal states values.
 	 * @param maxplayer The player that get max value.
	 * @return A new simulator.
	 */
	ISimulator createSimulator(StateMachine machine, IStateLabeler labeler, Role maxplayer);
}
