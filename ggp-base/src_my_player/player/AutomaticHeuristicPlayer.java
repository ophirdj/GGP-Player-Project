package player;

import heuristics.ClassifierBuilder.ClassifierBuildException;
import heuristics.HeuristicGenerator;
import heuristics.StateClassifier;

import java.util.List;

import minmax.HeuristicLimitedDepthMinMax;
import minmax.LimitedDepthMinMax;
import minmax.MinMax.MinMaxException;

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

public class AutomaticHeuristicPlayer extends StateMachineGamer {

	private StateClassifier classifier;
	private LimitedDepthMinMax minmax;
	private int turnNumber;

	public AutomaticHeuristicPlayer() {
		this.classifier = null;
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
		HeuristicGenerator g = new HeuristicGenerator(getMatch().getGame(),
				getStateMachine(), getRole(), oponentRole);
		try {
			classifier = g.generateClassifier(300);
			minmax = new HeuristicLimitedDepthMinMax(getStateMachine(),
					getRole(), classifier);
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
		long start = System.currentTimeMillis();

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(),
				getRole());
		Move selection = null;
		try {
			if (moves.size() > 1) {
				selection = minmax.bestMove(new MyState(getCurrentState(),
						turnNumber, getRole()));
			}
		} catch (MinMaxException e) {
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
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}

}
