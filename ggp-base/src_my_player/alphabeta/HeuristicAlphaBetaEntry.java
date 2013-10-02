package alphabeta;

import org.ggp.base.util.statemachine.Move;

public class HeuristicAlphaBetaEntry extends AlphaBetaEntry {

	private double alpha;
	private double beta;

	public HeuristicAlphaBetaEntry(double alpha, double beta, Move move,
			int height) {
		this.alpha = alpha;
		this.beta = beta;
		this.move = move;
		this.height = height;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getBeta() {
		return beta;
	}
}
