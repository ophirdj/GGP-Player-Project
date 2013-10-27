package minmax;

import minmax.anytime.AnyTimeMinMax;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import stateclassifier.IStateClassifier;

public abstract class IMinMaxFactory {

	private static final int DEFAULT_DEPTH = 2;

	/**
	 * 
	 * @param machine
	 *            State machine initialized with the game.
	 * @param maxPlayer
	 *            The player who larger state values are good for him.
	 * @param classifier
	 *            A Classifier for the game.
	 * @param cached Should algorithm use cache?
	 * @param anytime
	 *            Should algorithm be any time?
	 * @return A new object that implements a (variation of) min-max algorithm.
	 */
	public final IMinMax createMinMax(StateMachine machine, Role maxPlayer,
			IStateClassifier classifier, boolean cached, boolean anytime) {
		IMinMax minmax = createLimitedDepthMinMax(machine, maxPlayer,
				classifier, DEFAULT_DEPTH, cached);
		if (anytime) {
			return new AnyTimeMinMax(minmax);
		}
		return minmax;
	}

	protected abstract IMinMax createLimitedDepthMinMax(StateMachine machine,
			Role maxPlayer, IStateClassifier classifier, int depth, boolean cached);
}
