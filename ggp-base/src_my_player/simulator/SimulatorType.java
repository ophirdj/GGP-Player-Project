package simulator;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;


public enum SimulatorType {
	BASIC_SIMULATOR{
		@Override
		public String toString() {
			return "terminal states";
		}

		@Override
		public MapValueSimulatorFactory getSimulatorFactory() {
			return new MapValueSimulatorFactory(){

				@Override
				public MapValueSimulator createSimulator(StateMachine machine,
						Role myRole, Role oponentRole) {
					return new Simulator(machine, myRole, oponentRole);
				}
				
				@Override
				public String toString() {
					return "Basic simulator factory";
				}
				
			};
		}
	},
	KNOWN_VALUE_SIMULATOR{

		@Override
		public String toString() {
			return "known value";
		}
		
		@Override
		public MapValueSimulatorFactory getSimulatorFactory() {
			return new MapValueSimulatorFactory(){

				@Override
				public MapValueSimulator createSimulator(StateMachine machine,
						Role myRole, Role oponentRole) {
					return new KnownValueSimulator(machine, myRole, oponentRole);
				}
				
				@Override
				public String toString() {
					return "Known values simulator factory";
				}
			};
		}
		
	}
	
	;
	
	public abstract MapValueSimulatorFactory getSimulatorFactory();
}
