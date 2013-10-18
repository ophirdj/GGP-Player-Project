package labeler.minmax;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import labeler.IStateLabeler;
import labeler.IStateLabelerFactory;

public final class MinMaxValueStateLabelerFactory implements IStateLabelerFactory {

	@Override
	public IStateLabeler createStateLabeler(StateMachine machine, Role player) {
		return new MinMaxValueStateLabeler(machine, player);
	}

	@Override
	public String toString() {
		return MinMaxValueStateLabeler.class.getSimpleName();
	}
}
