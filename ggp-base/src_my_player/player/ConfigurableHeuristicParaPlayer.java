package player;

import heuristics.ClassifierBuilder;
import heuristics.ClassifierBuilder.ClassifierBuildException;
import heuristics.HeuristicGenerator;
import heuristics.StateClassifier;
import heuristics.StateClassifier.ClassificationException;

import java.util.List;

import minmax.LimitedDepthMinMax;
import minmax.MinMax.MinMaxException;
import minmax.MinMaxFactory;
import minmax.MinMaxType;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.observer.Subject;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import simulator.MapValueSimulator;
import simulator.MapValueSimulatorFactory;
import simulator.SimulatorType;
import state.MyState;
import debugging.Verbose;

public class ConfigurableHeuristicParaPlayer extends ParaStateMachinePlayer{

	private StateClassifier classifier;
	private LimitedDepthMinMax minmax;
	private MinMaxFactory minmaxFactory;
	private MapValueSimulatorFactory simulatorFactory;
	private int exampleAmount;
	private boolean isInitialize;
	
	
	private int turnNumber;
	private ClassifierBuilder classifierBuilder;
	private int minmaxDepth;
	private MapValueSimulator simulator;
	private Role oponent;

	public ConfigurableHeuristicParaPlayer(StateMachineGamer caller) {
		super(caller);
		this.turnNumber = 0;
		this.isInitialize = false;
		this.oponent = null;
	}
	
	@Override
	public void initialize(int exampleAmount, MapValueSimulatorFactory mapValueSimulatorFactory, MinMaxFactory minmaxFactory, BuilderFactory builderFactory, int minmaxDepth){
		isInitialize = true;
		this.exampleAmount = exampleAmount;
		this.simulatorFactory = mapValueSimulatorFactory;
		this.minmaxFactory = minmaxFactory;
		this.classifierBuilder = builderFactory.createClassifierBuilder();
		this.minmaxDepth = minmaxDepth;
	}
	


	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException,
			GoalDefinitionException {
		if(!isInitialize){
			Verbose.printVerboseError("Heuristic player is not initialize, use default values", Verbose.UNIMPLEMENTED_OPTION);
			defaultInitialization();
		}
		StateMachine machine = getStateMachine();
		simulator = buildSimulator();
		addAllObservers(simulator);
		HeuristicGenerator g = new HeuristicGenerator(getMatch().getGame(),
				machine, simulator, classifierBuilder);
		addAllObservers(g);
		try {
			classifier = g.generateClassifier(exampleAmount);
			minmax = minmaxFactory.createHeuristicLimitedDepthMinMax(machine, getRole(), classifier);
			addAllObservers(minmax);
			minmax.setDepth(minmaxDepth);
		} catch (ClassifierBuildException e) {
			e.printStackTrace();
		}
	}

	private void addAllObservers(Subject subject) {
		for(Observer observer: this.observers) {
			subject.addObserver(observer);
		}
	}

	private MapValueSimulator buildSimulator() {
		StateMachine machine = getStateMachine();
		Role myRole = getRole();
		Role oponentRole = (myRole.equals(machine.getRoles().get(0))) ? machine.getRoles().get(1) : machine.getRoles().get(0);
		return simulatorFactory.createSimulator(machine, myRole, oponentRole);
	}


	private void defaultInitialization() {
		isInitialize = true;
		this.exampleAmount = 100;
		this.simulatorFactory = SimulatorType.BASIC_SIMULATOR.getSimulatorFactory();
		this.minmaxFactory = MinMaxType.BASIC_HUERISTIC_MINMAX.getFactory();
		this.classifierBuilder = BuilderType.SIMPLE.getBuilderFactory().createClassifierBuilder();
		this.minmaxDepth = 3;
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
		// TODO Auto-generated method stub
		return getClass().getSimpleName();
	}

}
