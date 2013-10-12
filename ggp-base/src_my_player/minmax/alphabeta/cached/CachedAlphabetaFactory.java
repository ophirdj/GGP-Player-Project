package minmax.alphabeta.cached;

import minmax.IMinMax;
import minmax.IMinMaxFactory;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;

public class CachedAlphabetaFactory implements IMinMaxFactory {

	@Override
	public IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		return new CachedAlphaBeta(machine, maxPlayer, classifier);
	}

	@Override
	public String toString() {
		return CachedAlphaBeta.class.getSimpleName();
	}

}
