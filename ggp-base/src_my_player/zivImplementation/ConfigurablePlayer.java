package zivImplementation;

import interfaces.IClassifier;
import interfaces.IClassifierFactory;
import interfaces.IMinMax;
import interfaces.IMinMax.MinMaxException;
import interfaces.ISimulator;
import interfaces.IStateLabeler;

import java.util.List;

import org.ggp.base.apps.player.config.ConfigPanel;
import org.ggp.base.apps.player.detail.DetailPanel;
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

import playerstatistics.PlayerDetatilPanel;
import state.MinMaxValueStateLabeler;
import state.MyState;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;

public class ConfigurablePlayer extends StateMachineGamer {

	private ConfigurationPanel configPanel;
	private PlayerDetatilPanel detatilPanel;
	private IMinMax minmax;
	private int turnNumber;

	public ConfigurablePlayer() {
		this.configPanel = new ConfigurationPanel();
		this.detatilPanel = new PlayerDetatilPanel(getName(),
				configPanel.savePlayerData);
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		configPanel.setEditble(false);
		StateMachine machine = getStateMachine();
		IStateLabeler labeler = new MinMaxValueStateLabeler(machine, getRole());
		ISimulator simulator = configPanel.getSimulatorFactory()
				.createSimulator(machine, labeler, getRole());
		MyState initalState = buildInitialState(machine);
		int exampleAmount = configPanel.getExampleAmount();
		for (int counter = 0; counter < exampleAmount; counter++) {
			simulator.Simulate(initalState);
		}
		Classifier wekaClassifier = new LinearRegression();
		Game game = getMatch().getGame();
		IClassifierFactory classifierFactory = configPanel.getStateClassifierFactory();
		try {
			IClassifier classifier = classifierFactory.createClassifier(game.getName(),
							simulator.getAllContents(),
							game.getRules(),
							simulator.getLabeledStates(), wekaClassifier);
			this.minmax = configPanel.getMinmaxFactory().createMinMax(machine, getRole(), classifier);
			minmax.setDepth(configPanel.getMinMaxDepth());
		} catch (ClassifierBuildingException e) {
			e.printStackTrace();
		}
		turnNumber = 0;
		configPanel.setEditble(true);
	}

	private static MyState buildInitialState(StateMachine machine) {
		Role startingRole = machine.getRoles().get(0);
		Role secondRole = machine.getRoles().get(1);
		return new MyState(machine.getInitialState(), 0, startingRole,
				secondRole);
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
				selection = minmax.getMove(new MyState(getCurrentState(),
						turnNumber, getRole(), getOponent()));
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

	private Role getOponent() {
		StateMachine machine = getStateMachine();
		Role myRole = getRole();
		List<Role> roles = machine.getRoles();
		return roles.get(0).equals(myRole) ? roles.get(1) : roles
				.get(0);
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
		return getClass().getSimpleName();
	}

	@Override
	public ConfigPanel getConfigPanel() {
		return configPanel;
	}

	@Override
	public DetailPanel getDetailPanel() {
		return detatilPanel;
	}

}
