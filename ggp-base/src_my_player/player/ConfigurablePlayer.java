package player;

import java.util.List;

import labeler.IStateLabeler;
import minmax.IMinMax;
import minmax.IMinMax.MinMaxException;

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

import playerdetails.ConfigurablePlayerDetailPanel;
import simulator.ISimulator;
import states.MyState;
import utils.Verbose;
import weka.classifiers.Classifier;
import classifier.IClassifier;
import classifier.IClassifierFactory;
import classifier.IClassifierFactory.ClassifierBuildingException;

public class ConfigurablePlayer extends StateMachineGamer {

	private static int numInstances = 0;

	private final int myNumber;
	private final ConfigurationPanel configurationPanel;
	private final DetailPanel detatilPanel;
	private IMinMax minmax;
	private int turnNumber;

	public ConfigurablePlayer() {
		this.myNumber = numInstances++;
		this.configurationPanel = new ConfigurationPanel();
		this.detatilPanel = new ConfigurablePlayerDetailPanel(getName(),
				configurationPanel.savePlayerData);
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		configurationPanel.setEnabled(false);
		Verbose.printVerboseNoNewLine("getting machine... ", Verbose.PLAYER);
		StateMachine machine = getStateMachine();
		Verbose.printVerbose("success", Verbose.PLAYER);
		Verbose.printVerboseNoNewLine("getting labeler... ", Verbose.PLAYER);
		IStateLabeler labeler = configurationPanel.getLabelerFactory()
				.createStateLabeler(getStateMachine(), getRole());
		Verbose.printVerbose("success", Verbose.PLAYER);
		Verbose.printVerboseNoNewLine("getting simulator... ", Verbose.PLAYER);
		ISimulator simulator = configurationPanel.getSimulatorFactory()
				.createSimulator(machine, labeler, getRole());
		Verbose.printVerbose("success", Verbose.PLAYER);
		simulator.addObserver(detatilPanel);
		Verbose.printVerboseNoNewLine("building initial state... ",
				Verbose.PLAYER);
		MyState initalState = buildInitialState();
		Verbose.printVerbose("success", Verbose.PLAYER);
		Verbose.printVerboseNoNewLine("getting number of examples... ",
				Verbose.PLAYER);
		int exampleAmount = configurationPanel.getExampleAmount();
		Verbose.printVerbose("success", Verbose.PLAYER);
		for (int counter = 0; counter < exampleAmount; counter++) {
			simulator.Simulate(initalState);
			Verbose.printVerbose("current simulation is: " + counter + " of: "
					+ exampleAmount, Verbose.CURRENT_SIMULATION_VERBOSE);
		}

		Verbose.printVerboseNoNewLine("getting classifier type... ",
				Verbose.PLAYER);
		Classifier wekaClassifier = configurationPanel.getWekaClassifer();
		Verbose.printVerbose("success", Verbose.PLAYER);
		Game game = getMatch().getGame();
		IClassifierFactory classifierFactory = configurationPanel
				.getClassifierFactory();
		try {
			Verbose.printVerboseNoNewLine("getting classifier... ",
					Verbose.PLAYER);
			IClassifier classifier = classifierFactory.createClassifier(
					labeler, game.getName(), simulator.getAllContents(),
					game.getRules(), simulator.getLabeledStates(),
					wekaClassifier);
			Verbose.printVerbose("success", Verbose.PLAYER);
			Verbose.printVerboseNoNewLine("getting minmax... ", Verbose.PLAYER);
			this.minmax = configurationPanel.getMinmaxFactory().createMinMax(
					machine, getRole(), classifier,
					configurationPanel.isMinMaxCached(),
					configurationPanel.isMinMaxAnytime());
			Verbose.printVerbose("success", Verbose.PLAYER);
			if (!configurationPanel.isMinMaxAnytime()) {
				Verbose.printVerboseNoNewLine("setting minmax depth... ",
						Verbose.PLAYER);
				minmax.setDepth(configurationPanel.getMinMaxDepth());
				Verbose.printVerbose("success", Verbose.PLAYER);
			}
			minmax.addObserver(detatilPanel);
		} catch (ClassifierBuildingException e) {
			e.printStackTrace();
			Verbose.printVerboseError("configurable player " + myNumber
					+ " could not build classifier", Verbose.UNEXPECTED_VALUE);
		}
		turnNumber = 0;
		configurationPanel.setEnabled(true);
	}

	private MyState buildInitialState() throws MoveDefinitionException {
		StateMachine machine = getStateMachine();
		Role myRole = getRole();
		Role otherRole = machine.getRoles().get(0).equals(myRole) ? machine
				.getRoles().get(1) : machine.getRoles().get(0);
		if (machine.getLegalMoves(machine.getInitialState(), getRole()).size() > 1) {
			return new MyState(machine.getInitialState(), 0, myRole, otherRole);
		} else {
			return new MyState(machine.getInitialState(), 0, otherRole, myRole);
		}
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
				minmax.setTimeout(timeout - 1500);
				selection = minmax.getMove(new MyState(getCurrentState(),
						turnNumber, getRole(), getOponent()));
			}
		} catch (MinMaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// computation timed out
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
		return roles.get(0).equals(myRole) ? roles.get(1) : roles.get(0);
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
		// do nothing (not supposed to be implemented)
	}

	@Override
	public String getName() {
		if (myNumber <= 0) {
			return getClass().getSimpleName();
		} else {
			return getClass().getSimpleName() + myNumber;
		}
	}

	@Override
	public ConfigPanel getConfigPanel() {
		return configurationPanel;
	}

	@Override
	public DetailPanel getDetailPanel() {
		return detatilPanel;
	}

}
