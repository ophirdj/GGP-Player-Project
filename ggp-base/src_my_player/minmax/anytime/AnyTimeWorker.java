package minmax.anytime;

import minmax.IMinMax;
import minmax.IMinMax.MinMaxException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import states.MyState;
import utils.Verbose;

public final class AnyTimeWorker extends Thread {

	private final IMinMax minmax;
	private int maxDepth;
	private boolean startMinMaxCalled;
	private Move move;
	private MyState state;
	private long timeout;
	private boolean stopMinMaxCalled;
	private MinMaxException minMaxException;
	private MoveDefinitionException moveDefinitionException;
	private TransitionDefinitionException transitionDefinitionException;
	private GoalDefinitionException goalDefinitionException;

	public AnyTimeWorker(IMinMax minmax) {
		this.minmax = minmax;
		this.maxDepth = AnyTimeMinMax.DEFAULT_MINMAX_MAX_DEPTH;
		this.startMinMaxCalled = false;
		this.move = null;
		this.state = null;
		this.timeout = Long.MIN_VALUE;
		this.stopMinMaxCalled = false;
		clearExceptions();
	}

	@Override
	public void run() {
		while (true) {
			clearExceptions();
			final MyState state = waitForState();
			loopMinMaxAndSaveExceptions(state, timeout);
		}
	}

	private void loopMinMaxAndSaveExceptions(final MyState state,
			final long timeout) {
		try {
			loopMinMax(state, timeout);
		} catch (MinMaxException e) {
			Verbose.printVerbose("AnyTimeWorker: caught MinMaxException",
					Verbose.MIN_MAX_VERBOSE);
			this.minMaxException = e;
		} catch (MoveDefinitionException e) {
			Verbose.printVerbose(
					"AnyTimeWorker: caught MoveDefinitionException",
					Verbose.MIN_MAX_VERBOSE);
			this.moveDefinitionException = e;
		} catch (TransitionDefinitionException e) {
			Verbose.printVerbose(
					"AnyTimeWorker: caught TransitionDefinitionException",
					Verbose.MIN_MAX_VERBOSE);
			this.transitionDefinitionException = e;
		} catch (GoalDefinitionException e) {
			Verbose.printVerbose(
					"AnyTimeWorker: caught GoalDefinitionException",
					Verbose.MIN_MAX_VERBOSE);
			this.goalDefinitionException = e;
		}
	}

	private void clearExceptions() {
		Verbose.printVerbose("AnyTimeWorker: clearing exceptions",
				Verbose.MIN_MAX_VERBOSE);
		this.minMaxException = null;
		this.moveDefinitionException = null;
		this.transitionDefinitionException = null;
		this.goalDefinitionException = null;
	}

	private void loopMinMax(final MyState state, final long timeout)
			throws MinMaxException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		move = null;
		for (int depth = 1; depth < maxDepth; ++depth) {
			Verbose.printVerbose("AnyTimeWorker: starting minmax with depth "
					+ depth, Verbose.MIN_MAX_VERBOSE);
			minmax.setDepth(depth);
			try {
				Move result = minmax.getMove(state);
				synchronized (this) {
					move = result;
				}
			} catch (InterruptedException e) {
				if (stopMinMaxCalled) {
					stopMinMaxCalled = false;
					return;
				} else {
					Verbose.printVerboseError(
							"AnyTimeWorker: unknown interrupt during minmax",
							Verbose.UNEXPECTED_VALUE);
				}
			}
		}
	}

	public synchronized Move getMove() throws MinMaxException,
			MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		if (minMaxException != null) {
			throw minMaxException;
		} else if (moveDefinitionException != null) {
			throw moveDefinitionException;
		} else if (transitionDefinitionException != null) {
			throw transitionDefinitionException;
		} else if (goalDefinitionException != null) {
			throw goalDefinitionException;
		}
		return move;
	}

	private synchronized MyState waitForState() {
		while (!startMinMaxCalled) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
		startMinMaxCalled = false;
		return state;
	}

	public synchronized void startMinMaxLoop(MyState state, long timeout) {
		startMinMaxCalled = true;
		this.state = state;
		this.timeout = timeout;
		this.notify();
	}

	public synchronized void stopMinMaxLoop() {
		stopMinMaxCalled = true;
		this.interrupt();
	}

	public void setMaxDepth(int depth) {
		this.maxDepth = depth;
	}

}
