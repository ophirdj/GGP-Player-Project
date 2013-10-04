package minmax;

import heuristics.StateClassifier;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import statecompare.StateComparer;
import debugging.Verbose;
import alphabeta.HeuristicLimitedDepthAlphaBeta;
import alphabeta.StateCompareLimitedDepthAlphaBeta;

public enum MinMaxType {
	BASIC_HUERISTIC_MINMAX{
		@Override
		public String toString() {
			return "min max";
		}

		@Override
		public MinMaxFactory getFactory() {
			return new MinMaxFactory(){
				@Override
				public LimitedDepthMinMax createHeuristicLimitedDepthMinMax(
						StateMachine machine, Role maxPlayer, StateClassifier classifier) {
					return new HeuristicLimitedDepthMinMax(machine, maxPlayer, classifier);
				}

				@Override
				public LimitedDepthMinMax createCompareLimitedDepthMinMax(
						StateMachine machine, Role maxPlayer, StateComparer comparer) {
					Verbose.printVerboseError("basic type not supported", Verbose.UNEXPECTED_VALUE);
					return new StateCompareLimitedDepthAlphaBeta(machine, maxPlayer, comparer);
				}
				
				@Override
				public String toString() {
					return "Basic limited depth minmax";
				}
			};
		}
	}
	,
	ALPHA_BETA_MIN_MAX{
		@Override
		public String toString() {
			return "alpha beta";
		}

		@Override
		public MinMaxFactory getFactory() {
			return new MinMaxFactory(){
				@Override
				public LimitedDepthMinMax createHeuristicLimitedDepthMinMax(
						StateMachine machine, Role maxPlayer, StateClassifier classifier) {
					return new HeuristicLimitedDepthAlphaBeta(machine, maxPlayer, classifier);
				}

				@Override
				public LimitedDepthMinMax createCompareLimitedDepthMinMax(
						StateMachine machine, Role maxPlayer, StateComparer comparer) {
					return new StateCompareLimitedDepthAlphaBeta(machine, maxPlayer, comparer);
				}
				
				@Override
				public String toString() {
					return "Alpha-Beta limited depth minmax";
				}
			};
		}
		
		
	}
	
	;
	
	public abstract MinMaxFactory getFactory();
}
