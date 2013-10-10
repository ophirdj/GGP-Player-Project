package minmax;


import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;

public interface IMinMaxFactory {
	
	/**
	 * 
	 * @param machine The state machine of the game.
	 * @param maxPlayer The player that get max value.
	 * @param classifier A Classifier for the games.
	 * @return A new object that implement IMinMax 
	 */
	IMinMax createMinMax(StateMachine machine, Role maxPlayer, IClassifier classifier);
}
