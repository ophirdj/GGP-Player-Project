package player;

import org.ggp.base.apps.player.config.ConfigPanel;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

public class ConfigurablePlayer extends StateMachineGamer {

	private ConfigurationPanel configPanel;
	private ParaStateMachinePlayer copiedPlayer;

	public ConfigurablePlayer() {
		this.configPanel = new ConfigurationPanel();
		this.copiedPlayer = null;
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {

		copiedPlayer = configPanel.getParaPlayerFactory()
				.createParaStateMachinePlayer(this);
		copiedPlayer.initialize(configPanel.getExampleAmount(),
				configPanel.getSimulatorFactory(),
				configPanel.getMinmaxFactory(),
				configPanel.getBuilderFactory(), configPanel.getMinMaxDepth());
		copiedPlayer.stateMachineMetaGame(timeout);
	}

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		return copiedPlayer.stateMachineSelectMove(timeout);
	}

	@Override
	public void stateMachineStop() {
		copiedPlayer.stateMachineStop();
	}

	@Override
	public void stateMachineAbort() {
		copiedPlayer.stateMachineAbort();
	}

	@Override
	public void analyze(Game g, long timeout) throws GameAnalysisException {
		copiedPlayer.analyze(g, timeout);
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
	public String toString() {
		String name = "Configurable Player: ";
		name += "Example amount =  " + configPanel.getExampleAmount() + ", ";
		name += "MinMax depth = " + configPanel.getMinMaxDepth() + ", " ;
		name += "Player type = " + configPanel.getParaPlayerFactory() + ", ";
		name += "Simulator =  " + configPanel.getSimulatorFactory() + ", ";
		name += "MinMax =  " + configPanel.getMinmaxFactory() + ", ";
		name += "classifier =  " + configPanel.getBuilderFactory() + ";";
		return name;
	}

}
