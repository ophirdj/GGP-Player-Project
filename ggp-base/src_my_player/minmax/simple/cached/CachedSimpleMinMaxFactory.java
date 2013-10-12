package minmax.simple.cached;

import minmax.IMinMax;
import minmax.IMinMaxFactory;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;

public class CachedSimpleMinMaxFactory implements IMinMaxFactory {

	@Override
	public IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		return new CachedSimpleMinMax(machine, maxPlayer, classifier);
	}

	@Override
	public String toString() {
		return CachedSimpleMinMax.class.getSimpleName();
	}

}
