package labeler.simple;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;

import labeler.IStateLabeler;
import labeler.IStateLabelerFactory;

public final class SimpleStateLabelerFactory implements IStateLabelerFactory {

	@Override
	public IStateLabeler createStateLabeler(StateMachine machine, Role player) {
		return new SimpleStateLabeler(machine, player);
	}

	@Override
	public String toString() {
		return SimpleStateLabeler.class.getSimpleName();
	}
}
