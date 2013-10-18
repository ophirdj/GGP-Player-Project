package simulator.terminalstates;

import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import simulator.ISimulator;
import simulator.ISimulatorFactory;

public final class TerminalStatesSimulatorFactory implements ISimulatorFactory {

	@Override
	public ISimulator createSimulator(StateMachine machine,
			IStateLabeler labeler, Role maxplayer) {
		return new TerminalStatesSimulator(machine, labeler);
	}

	@Override
	public String toString() {
		return TerminalStatesSimulator.class.getSimpleName();
	}
}
