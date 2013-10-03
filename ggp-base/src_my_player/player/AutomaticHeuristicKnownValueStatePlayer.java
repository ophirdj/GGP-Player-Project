package player;

import heuristics.ClassifierBuilder;
import heuristics.ClassifierBuilder.ClassifierBuildException;
import heuristics.HeuristicMapSimulatorGenerator;
import heuristics.SimpleRegressionClassifierBuilder;
import heuristics.StateClassifier;
import heuristics.StateClassifier.ClassificationException;

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

import simulator.KnownValueSimulator;
import simulator.MapValueSimulator;
import state.MyState;

public class AutomaticHeuristicKnownValueStatePlayer extends StateMachineGamer {

	private StateClassifier classifier;
	private LimitedDepthMinMax minmax;
	private int turnNumber;

	public AutomaticHeuristicKnownValueStatePlayer() {
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
		StateMachine machine = getStateMachine();
		MapValueSimulator minMaxSimulator = new KnownValueSimulator(machine, getRole(), oponentRole);
		ClassifierBuilder classifierBuilder = new SimpleRegressionClassifierBuilder();
		HeuristicMapSimulatorGenerator g = new HeuristicMapSimulatorGenerator(getMatch().getGame(),
				machine, minMaxSimulator, classifierBuilder);
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

	@Override
	public void stateMachineStop() {
		minmax.clear();
	}

	@Override
	public void stateMachineAbort() {
		minmax.clear();
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
