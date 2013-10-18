package labeler.simple;

import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import states.LabeledState;
import states.MyState;

public final class SimpleStateLabeler implements IStateLabeler {

	private StateMachine machine;
	private Role player;

	public SimpleStateLabeler(StateMachine machine, Role player) {
		this.machine = machine;
		this.player = player;
	}

	@Override
	public LabeledState label(MyState state) throws GoalDefinitionException {
		if (machine.isTerminal(state.getState())) {
			return createLabel(state, machine.getGoal(state.getState(), player));
		} else {
			return null;
		}
	}

	@Override
	public LabeledState createLabel(MyState state, double value) {
		return new LabeledState(state, value);
	}

	@Override
	public double getMinValue() {
		return 0;
	}

	@Override
	public double getMaxValue() {
		return 100;
	}

}
