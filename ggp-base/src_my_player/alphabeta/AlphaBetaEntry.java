package alphabeta;

import org.ggp.base.util.statemachine.Move;

public abstract class AlphaBetaEntry {

	protected Move move;
	protected int height;

	public Move getMove() {
		return move;
	}

	public int getHeight() {
		return height;
	}

}