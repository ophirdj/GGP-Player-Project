package player;

import heuristics.StateClassifier.ClassificationException;

import java.util.List;

import minmax.LimitedDepthMinMax;
import minmax.MinMax.MinMaxException;
import minmax.NoHeuristicLimitedDepthMinMax;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import state.MyState;

public class MeanMaxPlayer extends StateMachineGamer {

	private LimitedDepthMinMax minmax;
	private int turnNumber;
	private Role oponent;

	public MeanMaxPlayer() {
		this.minmax = null;
		this.turnNumber = 0;
		this.oponent = null;
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		minmax = new NoHeuristicLimitedDepthMinMax(getStateMachine(), getRole());
		minmax.setDepth(3);
	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		long start = System.currentTimeMillis();

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(),
				getRole());
		Move selection = null;
		try {
			if (moves.size() > 1) {
				selection = minmax.bestMove(new MyState(getCurrentState(),
						turnNumber, getRole(), getOponent()));
			}
		} catch (MinMaxException | ClassificationException e) {
			e.printStackTrace();
		} finally {
			if (selection == null) {
				selection = moves.get(0);
			}
		}

		++turnNumber;

		long stop = System.currentTimeMillis();

		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop
				- start));
		return selection;
	}

	private Role getOponent() {
		if (oponent == null) {
			List<Role> roles = getStateMachine().getRoles();
			oponent = roles.get(0).equals(getRole()) ? roles.get(1) : roles
					.get(0);
		}
		return oponent;
	}

	@Override
	public void stateMachineStop() {
		minmax.clear();
		oponent = null;
	}

	@Override
	public void stateMachineAbort() {
		minmax.clear();
		oponent = null;
	}

	@Override
	public void analyze(Game g, long timeout) throws GameAnalysisException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}

}
