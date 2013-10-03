package simulator;

import java.util.List;
import java.util.Map;

import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

public interface MapValueSimulator {

	List<MyState> simulate(Role controlingPlayer) throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException;
	Map<MyState, Integer> getStateValueMap();
}
