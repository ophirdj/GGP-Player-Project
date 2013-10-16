package minmax.limiteddepth;

import java.util.List;
import java.util.Map.Entry;

import minmax.IMinMax;
import minmax.MinMaxReporter;
import minmax.StateExpander;

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


public abstract class LimitedDepthMinMaxInfrastructure implements IMinMax {
	
	public static class MinMaxEntry {
		private final ClassifierValue value;
		private final Move bestMove;
		
		public MinMaxEntry(ClassifierValue value, Move bestMove) {
			Verbose.printVerbose("State value is " + value,
					Verbose.MIN_MAX_VERBOSE);
			this.value = value;
			this.bestMove = bestMove;
		}
		
		public ClassifierValue getValue() {
			return value;
		}
		
		public Move getMove() {
			return bestMove;
		}
	}

	private static final int DEFAULT_MINMAX_DEPTH = 2;
	
	protected StateMachine machine;
	protected IClassifier classifier;
	protected Role minPlayer;
	protected Role maxPlayer;
	protected MinMaxReporter reporter;
	private int minMaxDepth;
	
	public LimitedDepthMinMaxInfrastructure(StateMachine machine, Role maxPlayer,
			IClassifier classifier) {
		List<Role> roles = machine.getRoles();
		assert (roles.size() == 2);
		this.machine = machine;
		this.maxPlayer = maxPlayer;
		this.minPlayer = roles.get(0).equals(maxPlayer) ? roles.get(1) : roles.get(0);
		this.classifier = classifier;
		this.reporter = new MinMaxReporter();
		minMaxDepth = DEFAULT_MINMAX_DEPTH;
	}
	
	/**
	 * Check if a state is terminal.
	 * 
	 * @param state
	 * @return
	 */
	protected boolean isTerminal(MyState state) {
		return machine.isTerminal(state.getState());
	}

	/**
	 * Return the value of a state.
	 * 
	 * @param state
	 * @return
	 * @throws ClassificationException
	 */
	protected ClassifierValue getValue(MyState state)
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
	protected List<Entry<Move, MyState>> expand(MyState state)
			throws MoveDefinitionException, TransitionDefinitionException,
			ClassificationException {
		return StateExpander.expand(machine, state, classifier, maxPlayer);
	}
	
	/**
	 * Return true if (and only if) entry1 is better then entry2. Meaning, only
	 * return true if the player will want to switch to entry2.
	 */
	protected boolean isBetterThan(MinMaxEntry entry1, MinMaxEntry entry2,
			Role controlingPlayer) throws ClassificationException {
		return isBetterThan(entry1.getValue(), entry2.getValue(),
				controlingPlayer);
	}
	
	/**
	 * Return true if (and only if) value1 is better then value2.
	 */
	protected boolean isBetterThan(ClassifierValue value1,
			ClassifierValue value2, Role controlingPlayer)
					throws ClassificationException {
		if (controlingPlayer.equals(maxPlayer)) {
			return classifier.isBetterValue(value1, value2);
		} else {
			return classifier.isBetterValue(value2, value1);
		}
	}
	
	@Override
	public void addObserver(Observer observer) {
		reporter.addObserver(observer);
	}

	@Override
	public void notifyObservers(Event event) {
		reporter.notifyObservers(event);
	}

	public synchronized int getDepth() {
		return minMaxDepth;
	}

	@Override
	public synchronized void setDepth(int minMaxDepth) {
		this.minMaxDepth = minMaxDepth;
	}
	
	@Override
	public void finishBy(long timeout) {
		// do nothing
	}

}
