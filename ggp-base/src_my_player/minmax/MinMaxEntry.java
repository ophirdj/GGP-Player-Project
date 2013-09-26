package minmax;

import org.ggp.base.util.statemachine.Move;

public class MinMaxEntry {

	private static final int STARTING_TTL = 0;
	private final double value;
	private final Move bestMove;
	private final int importance;
	private int ttl;

	public MinMaxEntry(double d, Move bestMove, int importance) {
		this.value = d;
		this.bestMove = bestMove;
		this.importance = importance;
		this.ttl = STARTING_TTL + importance;
	}

	public void reduceTTL() {
		ttl--;
	}

	public boolean canPrune() {
		return ttl < 0;
	}

	public double getValue() {
		ttl = STARTING_TTL + importance;
		return value;
	}

	public Move getMove() {
		return bestMove;
	}

	public int getImportance() {
		return importance;
	}
}