package alphabeta;

import org.ggp.base.util.statemachine.Move;

import state.MyState;

public class StateCompareAlphaBetaEntry extends AlphaBetaEntry {

	private MyState alpha;
	private MyState beta;

	public StateCompareAlphaBetaEntry(MyState alpha, MyState beta, Move move,
			int height) {
		this.alpha = alpha;
		this.beta = beta;
		this.move = move;
		this.height = height;
	}

	public MyState getAlpha() {
		return alpha;
	}

	public MyState getBeta() {
		return beta;
	}
}
