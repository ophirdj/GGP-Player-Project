package minmax.limiteddepth;

import java.util.List;
import java.util.Map.Entry;

import minmax.IMinMax;
import minmax.limiteddepth.MinMaxCache.CacheEntry;

import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;
import utils.Verbose;
import classifier.IClassifier;
import classifier.IClassifier.ClassificationException;
import classifier.IClassifier.ClassifierValue;

public abstract class LimitedDepthMinMax implements IMinMax {

	public static class MinMaxEntry {

		public final ClassifierValue value;
		public final Move move;
		public final boolean noHeuristic;

		public MinMaxEntry(ClassifierValue value, Move move, boolean noHeuristic) {
			Verbose.printVerbose("State value is " + value,
					Verbose.MIN_MAX_VERBOSE);
			this.value = value;
			this.move = move;
			this.noHeuristic = noHeuristic;
		}
	}

	protected final StateMachine machine;
	private final IClassifier classifier;
	protected int minMaxDepth;
	protected final Role minPlayer;
	protected final Role maxPlayer;
	protected final MinMaxReporter reporter;
	private final MinMaxCache<MinMaxEntry> cache;

	public LimitedDepthMinMax(StateMachine machine, Role maxPlayer,
			IClassifier classifier, int depth, boolean cached) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = roles.get(0).equals(maxPlayer) ? roles.get(1) : roles
				.get(0);
		this.classifier = classifier;
		this.minMaxDepth = depth;
		this.reporter = new MinMaxReporter();
		this.cache = !cached ? null : new MinMaxCache<MinMaxEntry>();
	}

	/**
	 * Check if computation timed out.
	 * 
	 * @return True if someone interrupted the computation.
	 */
	protected final boolean isTimeout() {
		return Thread.currentThread().isInterrupted();
	}

	/**
	 * Check if a state is terminal.
	 * 
	 * @param state
	 * @return
	 */
	protected final boolean isTerminal(MyState state) {
		return machine.isTerminal(state.getState());
	}

	/**
	 * Return the value of a state.
	 * 
	 * @param state
	 * @return
	 * @throws ClassificationException
	 */
	protected final ClassifierValue getValue(MyState state)
			throws ClassificationException {
		return classifier.getValue(state);
	}

	/**
	 * Expand a state.
	 * 
	 * @param state
	 * @return A list of all children of given state (with their corresponding
	 *         moves) sorted from best to worst.
	 * @throws MoveDefinitionException
	 * @throws TransitionDefinitionException
	 * @throws ClassificationException
	 */
	protected final List<Entry<Move, MyState>> expand(MyState state)
			throws MoveDefinitionException, TransitionDefinitionException,
			ClassificationException {
		return StateExpander.expand(machine, state, classifier, maxPlayer);
	}

	/**
	 * Return true if (and only if) entry1 is better then entry2. Meaning, only
	 * return true if the player will want to switch to entry2.
	 */
	protected final boolean isBetterThan(MinMaxEntry entry1,
			MinMaxEntry entry2, Role controlingPlayer)
			throws ClassificationException {
		return isBetterThan(entry1.value, entry2.value, controlingPlayer);
	}

	/**
	 * Return true if (and only if) value1 is better then value2.
	 */
	protected final boolean isBetterThan(ClassifierValue value1,
			ClassifierValue value2, Role controlingPlayer)
			throws ClassificationException {
		if (controlingPlayer.equals(maxPlayer)) {
			return classifier.isBetterValue(value1, value2);
		} else {
			return classifier.isBetterValue(value2, value1);
		}
	}

	/**
	 * Add an entry to cache under a state(if exists).
	 * 
	 * @param state
	 *            Key in cache.
	 * @param entry
	 *            Value to be cached.
	 * @param height
	 *            Height of the result in search tree.
	 */
	protected final void addToCache(MyState state, MinMaxEntry entry,
			int height) {
		if (cache != null) {
			if (entry.noHeuristic) {
				cache.put(state, new CacheEntry<MinMaxEntry>(entry,
						CacheEntry.TERMINAL_STATE_DEPTH));
			} else {
				cache.put(state, new CacheEntry<MinMaxEntry>(entry, height));
			}
		}
	}

	/**
	 * Search an entry in the cache.
	 * 
	 * @param state
	 *            What we search.
	 * @param height
	 *            What height we look for.
	 * @return entry exists or null otherwise.
	 */
	protected final MinMaxEntry searchCache(MyState state, int height) {
		if (cache == null || !cache.contains(state, height)) {
			return null;
		}
		reporter.cacheHit();
		return cache.get(state);
	}

	@Override
	public final void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public final void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

	@Override
	public final void setDepth(int depth) {
		this.minMaxDepth = depth;
	}

	@Override
	public final void setTimeout(long timeout) {
		// do nothing
	}

	@Override
	public void clear() {
		reporter.resetCount();
		if (cache != null) {
			cache.clear();
		}
	}

}
