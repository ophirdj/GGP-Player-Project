package simulator.terminalstates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import simulator.BaseSimulator;
import states.LabeledState;
import states.MyState;

public class TerminalStatesSimulator extends BaseSimulator {

	private Set<LabeledState> labeled;

	public TerminalStatesSimulator(StateMachine machine, IStateLabeler labeler) {
		super(machine, labeler);
		this.labeled = new HashSet<LabeledState>();
	}

	@Override
	public void Simulate(MyState rootState) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		List<MyState> simulation = new ArrayList<MyState>();
		MyState state = rootState;
		while (!machine.isTerminal(state.getState())) {
			simulation.add(state);
			addContents(state);
			MachineState nextState = machine.getRandomNextState(state
					.getState());
			state = MyState.createChild(state, nextState);
		}
		simulation.add(state);
		addContents(state);
		labeled.add(labeler.label(simulation.get(simulation.size() - 1)));
	}

	@Override
	public Collection<LabeledState> getLabeledStates() {
		return labeled;
	}

}
