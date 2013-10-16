package minmax.anytime.alphabeta;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import classifier.IClassifier;
import minmax.IMinMax;
import minmax.IMinMaxFactory;

public class AnyTimeAlphaBetaFactory implements IMinMaxFactory {

	@Override
	public IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		return new AnyTimeAlphaBeta(machine, maxPlayer, classifier);
	}
	
	@Override
	public String toString() {
		return AnyTimeAlphaBeta.class.getSimpleName();
	}

}
