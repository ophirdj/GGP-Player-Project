package player;

import heuristics.ClassifierBuilder.ClassifierBuildException;
import heuristics.StateClassifier.ClassificationException;

import java.util.List;

import minmax.LimitedDepthMinMax;
import minmax.MinMax.MinMaxException;

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

import alphabeta.StateCompareLimitedDepthAlphaBeta;
import state.MyState;
import statecompare.ComparerGenerator;
import statecompare.StateComparer;

public class AutomaticStateComparerAlphaBetaPlayer extends StateMachineGamer {

	private StateComparer clomparer;
	private LimitedDepthMinMax minmax;
	private int turnNumber;

	public AutomaticStateComparerAlphaBetaPlayer() {
		this.clomparer = null;
		this.minmax = null;
		this.turnNumber = 0;
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		System.out.println("META-GAME START");
		List<Role> roles = getStateMachine().getRoles();
		Role oponentRole = getRole().equals(roles.get(0)) ? roles.get(1)
				: roles.get(0);
		ComparerGenerator g = new ComparerGenerator(getMatch().getGame(),
				getStateMachine(), getRole(), oponentRole);
		try {
			clomparer = g.generateComparer(500);
			minmax = new StateCompareLimitedDepthAlphaBeta(getStateMachine(),
					getRole(), clomparer);
			minmax.setDepth(3);
		} catch (ClassifierBuildException e) {
			e.printStackTrace();
		}
		System.out.println("META-GAME END");
	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		System.out.println("SELECT MOVE START");
		long start = System.currentTimeMillis();

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(),
				getRole());
		Move selection = null;
		try {
			if (moves.size() > 1) {
				selection = minmax.bestMove(new MyState(getCurrentState(),
						turnNumber, getRole()));
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
		System.out.println("SELECT MOVE END");
		return selection;
	}

	@Override
	public void stateMachineStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineAbort() {
		// TODO Auto-generated method stub

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
