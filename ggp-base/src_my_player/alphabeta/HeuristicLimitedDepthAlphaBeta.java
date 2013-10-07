package alphabeta;

import heuristics.StateClassifier;
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

import alphabeta.StateGenerator.CompareFunction;
import debugging.Verbose;
import state.MyState;

public class HeuristicLimitedDepthAlphaBeta implements LimitedDepthAlphaBeta {

	private StateMachine machine;
	private Role maxPlayer;
	private Role minPlayer;
	private int searchDepth;
	private StateClassifier classifier;
	private AlphaBetaCache<HeuristicAlphaBetaEntry> cache;
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
				return classifier.classifyState(state1) > classifier.classifyState(state2);
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
				return classifier.classifyState(state1) < classifier.classifyState(state2);
			}
		}
	};

	public HeuristicLimitedDepthAlphaBeta(StateMachine machine, Role maxPlayer,
			StateClassifier classifier) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = (roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0));
		this.searchDepth = 2;
		this.classifier = classifier;
		this.cache = new AlphaBetaCache<HeuristicAlphaBetaEntry>();
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
		Move move;
		try {
			move = alphabeta(state, Double.NEGATIVE_INFINITY,
					Double.POSITIVE_INFINITY, searchDepth).getMove();
			long endTime = System.currentTimeMillis();
			reporter.reportAndReset(move, cache.size(), searchDepth, endTime - startTime);
			return move;
		//This should only happen if time was over timeout
		} catch (InterruptedException e) {
			return null;
		}
	}

	private HeuristicAlphaBetaEntry alphabeta(MyState state, double alpha,
			double beta, int depth) throws GoalDefinitionException,
			MoveDefinitionException, TransitionDefinitionException,
			ClassificationException, AlphaBetaException, InterruptedException {
		if (Thread.currentThread().isInterrupted()){
			throw new InterruptedException();
		}
		reporter.exploreNode();
		if (cache.containsKey(state, depth)) {
			reporter.cacheHit();
			return cache.get(state);
		}
		HeuristicAlphaBetaEntry entry = null;
		if (machine.isTerminal(state.getState())) {
			reporter.visitTerminal();
			double goalValue = (machine.getGoal(state.getState(), maxPlayer) - machine
					.getGoal(state.getState(), minPlayer)) * 10000;
			Verbose.printVerbose("Final State with goal value " + goalValue,
					Verbose.MIN_MAX_VERBOSE);
			entry = new HeuristicAlphaBetaEntry(goalValue, goalValue, null, AlphaBetaEntry.TERMINAL_STATE_HEIGHT);
		} else if (depth <= 0) {
			double heuristicValue = classifier.classifyState(state);
			Verbose.printVerbose("reached final depth with heuristic value "
					+ heuristicValue, Verbose.MIN_MAX_VERBOSE);
			entry = new HeuristicAlphaBetaEntry(heuristicValue, heuristicValue,
					null, 0);
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

	private HeuristicAlphaBetaEntry maxMove(MyState state, double alpha,
			double beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException, AlphaBetaException, InterruptedException {
		HeuristicAlphaBetaEntry maxEntry = null;
		List<Entry<Move, MyState>> children = stateGenerator.getNextStates(state,
				maxPlayer, minPlayer, maxComparer);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> child : children) {
			++nodesVisited;
			HeuristicAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (maxEntry == null || entry.getAlpha() > maxEntry.getAlpha()) {
				maxEntry = new HeuristicAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (entry.getAlpha() > alpha) {
				alpha = entry.getAlpha();
			}
			if (alpha >= beta) {
				reporter.prune(children.size() - nodesVisited);
				break;
			}
		}
		return maxEntry;
	}

	private HeuristicAlphaBetaEntry minMove(MyState state, double alpha,
			double beta, int depth) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException,
			ClassificationException, AlphaBetaException, InterruptedException {
		HeuristicAlphaBetaEntry minEntry = null;
		List<Entry<Move, MyState>> children = stateGenerator.getNextStates(state,
				minPlayer, maxPlayer, minComparer);
		reporter.expandNode(children.size());
		int nodesVisited = 0;
		for (Entry<Move, MyState> child : children) {
			++nodesVisited;
			HeuristicAlphaBetaEntry entry = alphabeta(state, alpha, beta,
					depth - 1);
			if (minEntry == null || entry.getBeta() < minEntry.getBeta()) {
				minEntry = new HeuristicAlphaBetaEntry(entry.getAlpha(),
						entry.getBeta(), child.getKey(), depth);
			}
			if (entry.getBeta() < beta) {
				beta = entry.getBeta();
			}
			if (alpha >= beta) {
				reporter.prune(children.size() - nodesVisited);
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
