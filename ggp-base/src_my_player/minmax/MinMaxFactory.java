package minmax;

import heuristics.StateClassifier;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import statecompare.StateComparer;

public interface MinMaxFactory {

	LimitedDepthMinMax createHeuristicLimitedDepthMinMax(StateMachine machine, Role maxPlayer, StateClassifier classifier);
	LimitedDepthMinMax createCompareLimitedDepthMinMax(StateMachine machine, Role maxPlayer, StateComparer classifier);
}
