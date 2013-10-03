package alphabeta;

import org.ggp.base.util.statemachine.Move;

public abstract class AlphaBetaEntry {
	
	public static final int TERMINAL_STATE_HEIGHT = Integer.MAX_VALUE;

	protected Move move;
	protected int height;

	public Move getMove() {
		return move;
	}

	public int getHeight() {
		return height;
	}

}