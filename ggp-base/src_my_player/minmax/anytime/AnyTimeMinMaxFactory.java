package minmax.anytime;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;
import minmax.IMinMax;
import minmax.IMinMaxFactory;
import minmax.alphabeta.cached.CachedAlphabetaFactory;

public class AnyTimeMinMaxFactory implements IMinMaxFactory {
	
	private CachedAlphabetaFactory factory = new CachedAlphabetaFactory();

	@Override
	public IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		return new AnyTimeMinMax(factory.createMinMax(machine, maxPlayer, classifier));
	}
	
	@Override
	public String toString() {
		return AnyTimeMinMax.class.getSimpleName() + "(" + factory.toString() + ")";
	}

}
