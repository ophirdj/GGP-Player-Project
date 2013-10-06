package player;

import debugging.Verbose;

public enum PlayerType {
	HEURISTIC_PLAYER{
		@Override
		public String toString() {
			return "Heuristic Player";
		}

		@Override
		public ParaStateMachinePlayerFactory getParaPlayerFactory() {
			return new ParaStateMachinePlayerFactory(){

				@Override
				public ParaStateMachinePlayer createParaStateMachinePlayer(
						ConfigurablePlayer caller) {
					return new ConfigurableHeuristicParaPlayer(caller);
				}
				
				@Override
				public String toString() {
					return "Heuristic player factory";
				}
			};
		}
	}
	,
	COMPARATOR_PLAYER{
		@Override
		public String toString() {
			return "Comparator Player";
		}

		@Override
		public ParaStateMachinePlayerFactory getParaPlayerFactory() {
			return new ParaStateMachinePlayerFactory(){

				@Override
				public ParaStateMachinePlayer createParaStateMachinePlayer(
						ConfigurablePlayer caller) {
					//return new ConfigurableComparerParaPlayer(caller);
					Verbose.printVerboseError("Not supported type", Verbose.UNIMPLEMENTED_OPTION);
					return new ConfigurableHeuristicParaPlayer(caller);
				}
				
				@Override
				public String toString() {
					return "Comparator Player factory";
				}
			};
		}
	}
	;
	
	public abstract ParaStateMachinePlayerFactory getParaPlayerFactory();
}
