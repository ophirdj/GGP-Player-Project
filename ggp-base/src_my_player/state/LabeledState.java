package state;

public class LabeledState {

	private MyState state;
	private double value;

	public LabeledState(MyState state, double value) {
		this.state = state;
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public MyState getState() {
		return state;
	}
	
}
