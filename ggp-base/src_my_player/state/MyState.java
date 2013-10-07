package state;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;

public final class MyState {

	private MachineState state;
	private int turn;
	private Role role;
	private Role oponent;

	public MyState(MachineState state, int turnNumber, Role role, Role oponent) {
		this.state = state;
		this.turn = turnNumber;
		this.role = role;
		this.oponent = oponent;
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

	public Role getRole() {
		return role;
	}

	public Role getOponent() {
		return oponent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MyState other = (MyState) obj;
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		return true;
	}

	public static MyState createChild(MyState parent, MachineState child) {
		return new MyState(child, parent.getTurnNumber() + 1,
				parent.getOponent(), parent.getRole());
	}

}
