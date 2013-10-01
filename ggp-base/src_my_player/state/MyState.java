package state;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;

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

}
