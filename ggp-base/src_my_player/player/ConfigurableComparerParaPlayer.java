package player;

import java.util.List;

import heuristics.ClassifierBuilder.ClassifierBuildException;
import heuristics.StateClassifier.ClassificationException;
import minmax.LimitedDepthMinMax;
import minmax.MinMaxFactory;
import minmax.MinMaxType;
import minmax.MinMax.MinMaxException;

import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GameAnalysisException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
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
import statecompare.ComparerBuilder;
import statecompare.ComparerGenerator;
import statecompare.StateComparer;
import debugging.Verbose;

public class ConfigurableComparerParaPlayer extends ParaStateMachinePlayer{

	private int turnNumber;
	private boolean isInitialize;
	private int exampleAmount;
	private MapValueSimulatorFactory simulatorFactory;
	private MinMaxFactory minmaxFactory;
	private ComparerBuilder comparerBuilder;
	private int minmaxDepth;
	private MapValueSimulator simulator;
	private StateComparer comparer;
	private LimitedDepthMinMax minmax;

	public ConfigurableComparerParaPlayer(StateMachineGamer caller) {
		super(caller);
		this.turnNumber = 0;
		this.isInitialize = false;
	}

	@Override
	public void initialize(int exampleAmount,
			MapValueSimulatorFactory mapValueSimulatorFactory,
			MinMaxFactory minmaxFactory, BuilderFactory builderFactory,
			int minmaxDepth) {
		isInitialize = true;
		this.exampleAmount = exampleAmount;
		this.simulatorFactory = mapValueSimulatorFactory;
		this.minmaxFactory = minmaxFactory;
		this.comparerBuilder = builderFactory.createComparerBuilder();
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
		ComparerGenerator g = new ComparerGenerator(getMatch().getGame(),
				machine, simulator, comparerBuilder);
		try {
			comparer = g.generateComparer(exampleAmount);
			minmax = minmaxFactory.createCompareLimitedDepthMinMax(machine, getRole(), comparer);
			minmax.setDepth(minmaxDepth);
		} catch (ClassifierBuildException e) {
			e.printStackTrace();
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
		this.comparerBuilder = BuilderType.SIMPLE.getBuilderFactory().createComparerBuilder();
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
