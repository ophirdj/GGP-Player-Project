package minmax.simpleMinMax;

import minmax.IMinMax;
import minmax.IMinMaxFactory;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;


public class SimpleMinMaxFactory implements IMinMaxFactory {

	@Override
	public IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		return new SimpleMinMax(machine, maxPlayer, classifier);
	}

	
	@Override
	public String toString() {
		return SimpleMinMax.class.getSimpleName();
	}
}
