package alphabeta;

import heuristics.StateClassifier.ClassificationException;

import java.util.List;
import java.util.Map.Entry;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;
import statecompare.StateComparer;
import alphabeta.StateGenerator.SortFunction;
import debugging.Verbose;

public class StateCompareLimitedDepthAlphaBeta implements LimitedDepthAlphaBeta {

	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private int searchDepth;
	private StateComparer comparer;
	private AlphaBetaCache<StateCompareAlphaBetaEntry> cache;

	private StateGenerator stateGenerator;

	private SortFunction maxSortFunction = new SortFunction() {

		@Override
		public boolean isGreater(MyState state1, MyState state2)
				throws ClassificationException {
			return comparer.compare(state1, state2) > 0;
		}
	};
	private SortFunction minSortFunction = new SortFunction() {

		@Override
		public boolean isGreater(MyState state1, MyState state2)
				throws ClassificationException {
			return comparer.compare(state1, state2) < 0;
		}
	};

	public StateCompareLimitedDepthAlphaBeta(StateMachine machine,
			Role maxPlayer, StateComparer comparer) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.searchDepth = 2;
		this.comparer = comparer;
		this.cache = new AlphaBetaCache<StateCompareAlphaBetaEntry>();
		this.stateGenerator = new StateGenerator(machine);
	}

	@Override
	public void setDepth(int depth) {
		searchDepth = depth;
	}

	@Override
	public Move bestMove(MyState state) throws MinMaxException {
		if (state == null) {
			throw new MinMaxException();
		}
		try {
			Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
			return alphabeta(state, null, null, searchDepth).getMove();
		} catch (GoalDefinitionException | MoveDefinitionException
				| TransitionDefinitionException | ClassificationException e) {
			e.printStackTrace();
			throw new AlphaBetaException();
		}
	}

	private StateCompareAlphaBetaEntry alphabeta(MyState state, MyState alpha,
			MyState beta, int depth) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException,
			ClassificationException {
		if (cache.containsKey(state, depth)) {
			return cache.get(state);
		}
		StateCompareAlphaBetaEntry entry = null;
		if (machine.isTerminal(state.getState())) {
			Verbose.printVerbose("Final State with goal value ",
					Verbose.MIN_MAX_VERBOSE);
			entry = new StateCompareAlphaBetaEntry(state, state, null, -1);
		} else if (depth <= 0) {
			Verbose.printVerbose("reached final depth", Verbose.MIN_MAX_VERBOSE);
			entry = new StateCompareAlphaBetaEntry(state, state, null, 0);
		} else if (state.getControlingPlayer() == maxPlayer) {
			Verbose.printVerbose("MAX PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			entry = maxMove(state, alpha, beta, depth);
		} else if (state.getControlingPlayer() == minPlayer) {
			Verbose.printVerbose("MIN PLAYER MOVE", Verbose.MIN_MAX_VERBOSE);
			entry = minMove(state, alpha, beta, depth);
		} else {
			throw new RuntimeException(
					"alpha-beta error: no match for controlingPlayer");
		}
		cache.put(state, entry);
		return entry;
	}

	private StateCompareAlphaBetaEntry maxMove(MyState state, MyState alpha,
			MyState beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException {
		StateCompareAlphaBetaEntry maxEntry = null;
		for (Entry<Move, MyState> child : stateGenerator.getNextStates(state,
				maxPlayer, minPlayer, maxSortFunction)) {
			StateCompareAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (maxEntry == null
					|| maxSortFunction.isGreater(entry.getAlpha(),
							maxEntry.getAlpha())) {
				maxEntry = new StateCompareAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (alpha == null || maxSortFunction.isGreater(entry.getAlpha(), alpha)) {
				alpha = entry.getAlpha();
			}
//			if (alpha != null && beta != null && !maxSortFunction.isGreater(beta, alpha)) {
//				break;
//			}
		}
		return maxEntry;
	}

	private StateCompareAlphaBetaEntry minMove(MyState state, MyState alpha,
			MyState beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException {
		StateCompareAlphaBetaEntry minEntry = null;
		for (Entry<Move, MyState> child : stateGenerator.getNextStates(state,
				minPlayer, maxPlayer, minSortFunction)) {
			StateCompareAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (minEntry == null
					|| minSortFunction.isGreater(entry.getBeta(),
							minEntry.getBeta())) {
				minEntry = new StateCompareAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (beta == null || minSortFunction.isGreater(entry.getBeta(), beta)) {
				beta = entry.getBeta();
			}
//			if (alpha != null && beta != null && !minSortFunction.isGreater(alpha, beta)) {
//				break;
//			}
		}
		return minEntry;
	}

}
