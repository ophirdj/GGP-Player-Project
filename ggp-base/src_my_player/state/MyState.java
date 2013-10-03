package state;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;

public class MyState {

	private MachineState state;
	private int turn;
	private Role role;

	public MyState(MachineState state, int turnNumber, Role controlingPlayer) {
		this.state = state;
		this.turn = turnNumber;
		this.role = controlingPlayer;
	}
	
	public MachineState getState() {
		return state;
	}

	public Set<GdlSentence> getContents() {
		return state.getContents();
	}

	public int getTurnNumber() {
		return turn;
	}
	
	public Role getControlingPlayer() {
		return role;
	}
	
	@Override
	public boolean equals(Object obj) {
		if((obj != null) && (obj instanceof MyState)){
			return state.equals(((MyState)obj).getState());
		}
		return false;
	}
	
	public int evaluateTerminalState(StateMachine machine, Role myRole) throws GoalDefinitionException{
		Role oponentRole = (machine.getRoles().get(0).equals(myRole)) ? machine.getRoles().get(1) : machine.getRoles().get(0);
		return machine.getGoal(state, myRole) - machine.getGoal(state, oponentRole);
	}

}
