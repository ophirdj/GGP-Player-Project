package alphabeta;

import heuristics.StateClassifier.ClassificationException;

import java.util.List;
import java.util.Map.Entry;

import minmax.MinMaxEventReporter;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;
import statecompare.StateComparer;
import alphabeta.StateGenerator.CompareFunction;
import debugging.Verbose;

public class StateCompareLimitedDepthAlphaBeta implements LimitedDepthAlphaBeta {

	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private int searchDepth;
	private StateComparer comparer;
	private AlphaBetaCache<StateCompareAlphaBetaEntry> cache;
	private StateGenerator stateGenerator;
	private MinMaxEventReporter reporter;

	private CompareFunction maxComparer = new CompareFunction() {

		@Override
		public boolean isGreater(MyState state1, MyState state2)
				throws ClassificationException, GoalDefinitionException {
			if (machine.isTerminal(state1.getState()) && machine.isTerminal(state2.getState())) {
				return machine.getGoal(state1.getState(), maxPlayer) > machine.getGoal(state2.getState(), maxPlayer);
			} else if (machine.isTerminal(state1.getState())) {
				return machine.getGoal(state1.getState(), maxPlayer) > machine.getGoal(state1.getState(), minPlayer);
			} else if (machine.isTerminal(state2.getState())) {
				return machine.getGoal(state2.getState(), maxPlayer) < machine.getGoal(state2.getState(), minPlayer);
			} else {
				return comparer.compare(state1, state2) > 0;
			}
		}
	};
	private CompareFunction minComparer = new CompareFunction() {

		@Override
		public boolean isGreater(MyState state1, MyState state2)
				throws ClassificationException, GoalDefinitionException {
			if (machine.isTerminal(state1.getState()) && machine.isTerminal(state2.getState())) {
				return machine.getGoal(state1.getState(), minPlayer) > machine.getGoal(state2.getState(), minPlayer);
			} else if (machine.isTerminal(state1.getState())) {
				return machine.getGoal(state1.getState(), maxPlayer) < machine.getGoal(state1.getState(), minPlayer);
			} else if (machine.isTerminal(state2.getState())) {
				return machine.getGoal(state2.getState(), maxPlayer) > machine.getGoal(state2.getState(), minPlayer);
			} else {
				return comparer.compare(state1, state2) < 0;
			}
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
		this.reporter = new MinMaxEventReporter();
	}

	@Override
	public void setDepth(int depth) {
		searchDepth = depth;
	}

	@Override
	public Move bestMove(MyState state) throws AlphaBetaException, GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException, ClassificationException {
		if (state == null) {
			throw new AlphaBetaException();
		}
		Verbose.printVerbose("START MINMAX", Verbose.MIN_MAX_VERBOSE);
		long startTime = System.currentTimeMillis();
		Move move = alphabeta(state, null, null, searchDepth).getMove();
		long endTime = System.currentTimeMillis();
		reporter.reportAndReset(move, cache.size(), searchDepth, endTime - startTime);
		return move;
	}

	private StateCompareAlphaBetaEntry alphabeta(MyState state, MyState alpha,
			MyState beta, int depth) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException,
			ClassificationException, AlphaBetaException {
		reporter.exploreNode();
		if (cache.containsKey(state, depth)) {
			reporter.cacheHit();
			return cache.get(state);
		}
		StateCompareAlphaBetaEntry entry = null;
		if (machine.isTerminal(state.getState())) {
			Verbose.printVerbose("Final State with goal value ",
					Verbose.MIN_MAX_VERBOSE);
			reporter.visitTerminal();
			entry = new StateCompareAlphaBetaEntry(state, state, null, AlphaBetaEntry.TERMINAL_STATE_HEIGHT);
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
			throw new AlphaBetaException(
					"alpha-beta error: no match for controlingPlayer");
		}
		cache.put(state, entry);
		return entry;
	}

	private StateCompareAlphaBetaEntry maxMove(MyState state, MyState alpha,
			MyState beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException, AlphaBetaException {
		StateCompareAlphaBetaEntry maxEntry = null;
		List<Entry<Move, MyState>> children = stateGenerator.getNextStates(state,
				maxPlayer, minPlayer, maxComparer);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> child : children) {
			++nodesVisited;
			StateCompareAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (maxEntry == null
					|| maxComparer.isGreater(entry.getAlpha(),
							maxEntry.getAlpha())) {
				maxEntry = new StateCompareAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (alpha == null || maxComparer.isGreater(entry.getAlpha(), alpha)) {
				alpha = entry.getAlpha();
			}
			if (alpha != null && beta != null && !maxComparer.isGreater(beta, alpha)) {
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		return maxEntry;
	}

	private StateCompareAlphaBetaEntry minMove(MyState state, MyState alpha,
			MyState beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException, AlphaBetaException {
		StateCompareAlphaBetaEntry minEntry = null;
		List<Entry<Move, MyState>> children = stateGenerator.getNextStates(state,
				minPlayer, maxPlayer, minComparer);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> child : children) {
			++nodesVisited;
			StateCompareAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (minEntry == null
					|| minComparer.isGreater(entry.getBeta(),
							minEntry.getBeta())) {
				minEntry = new StateCompareAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (beta == null || minComparer.isGreater(entry.getBeta(), beta)) {
				beta = entry.getBeta();
			}
			if (alpha != null && beta != null && !minComparer.isGreater(alpha, beta)) {
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		return minEntry;
	}

	@Override
	public void clear() {
		cache.clear();
	}
	
	@Override
	public void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

}
