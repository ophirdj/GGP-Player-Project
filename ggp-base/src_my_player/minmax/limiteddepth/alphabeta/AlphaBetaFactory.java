package minmax.limiteddepth.alphabeta;

import minmax.IMinMax;
import minmax.IMinMaxFactory;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import stateclassifier.IStateClassifier;


public class AlphaBetaFactory extends IMinMaxFactory {
	
	@Override
	protected IMinMax createLimitedDepthMinMax(StateMachine machine,
			Role maxPlayer, IStateClassifier classifier, int depth, boolean cached) {
		return new AlphaBeta(machine, maxPlayer, classifier, depth, cached);
	}

	@Override
	public String toString() {
		return AlphaBeta.class.getSimpleName();
	}
	
}
