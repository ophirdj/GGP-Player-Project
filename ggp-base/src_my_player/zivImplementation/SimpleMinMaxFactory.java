package zivImplementation;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import interfaces.IClassifier;
import interfaces.IMinMax;
import interfaces.IMinMaxFactory;

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
