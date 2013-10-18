package labeler.minmax;

import java.util.List;




import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

import states.LabeledState;
import states.MyState;


public final class MinMaxValueStateLabeler implements IStateLabeler {

	private StateMachine machine;
	private int maxPlayerIndex;
	private int minPlayerIndex;

	public MinMaxValueStateLabeler(StateMachine machine, Role maxplayer) {
		this.machine = machine;
		assert (machine.getRoles().size() == 2);
		maxPlayerIndex = machine.getRoles().get(0).equals(maxplayer) ? 0 : 1;
		minPlayerIndex = 1 - maxPlayerIndex;
	}

	@Override
	public LabeledState label(MyState state) throws GoalDefinitionException {
		if (machine.isTerminal(state.getState())) {
			List<Integer> goals = machine.getGoals(state.getState());
			return createLabel(state,
					goals.get(maxPlayerIndex) - goals.get(minPlayerIndex));
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
		return -100;
	}

	@Override
	public double getMaxValue() {
		return 100;
	}

}
