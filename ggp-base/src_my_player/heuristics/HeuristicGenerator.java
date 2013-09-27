package heuristics;

import heuristics.ClassifierBuilder.ClassifierBuildException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import debugging.Verbose;

import simulator.Simulator;
import state.MyState;
import weka.core.Instance;
import weka.core.Instances;

public class HeuristicGenerator {
	private List<Gdl> rules;
	private String gameName;
	private StateMachine machine;
	private Role myRole;
	private Role oponentRole;
	private Simulator simulator;
	private FeatureExtractor featureExtractor;
	private ClassifierBuilder classifierBuilder;

	public HeuristicGenerator(Game game, StateMachine machine, Role myRole,
			Role oponentRole) {
		this.rules = game.getRules();
		this.gameName = game.getName();
		this.machine = machine;
		this.myRole = myRole;
		this.oponentRole = oponentRole;
		this.simulator = new Simulator(machine);
		this.featureExtractor = null;
		this.classifierBuilder = new SimpleRegressionClassifierBuilder();
	}

	public StateClassifier generateClassifier(int numExamples)
			throws ClassifierBuildException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		List<LabeledExample> labeledStates = new ArrayList<HeuristicGenerator.LabeledExample>(
				numExamples);
		Set<MyState> allStates = new HashSet<MyState>();
		Verbose.printVerbose("SIMULATIONS START", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		for (int i = 0; i < numExamples; i++) {
			List<MyState> simulation = simulator.simulate(machine.getRoles()
					.get(0));
			allStates.addAll(simulation);
			MyState state = simulation.get(simulation.size() - 1);
			labeledStates.add(new LabeledExample(state, getStateLabel(state)));
			Verbose.printVerbose("Simulation " + i + " of " + numExamples + " finished", Verbose.CURRENT_SIMULATION_VERBOSE);
		}
		Verbose.printVerbose("SIMULATIONS END", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Verbose.printVerbose("FEATURE EXTRACTION START", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		featureExtractor = new AutomaticFeatureExtractor(gameName, rules, allStates);
		Verbose.printVerbose("FEATURE EXTRACTION END", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Verbose.printVerbose("CLASSIFY INSTANCES START", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Instances classifiedInstances = getClassifiedInstances(featureExtractor,
				labeledStates);
		Verbose.printVerbose("CLASSIFY INSTANCES END", Verbose.HEURISTIC_GENERATOR_VERBOSE);
		return classifierBuilder.buildClassifier(classifiedInstances, featureExtractor);
	}

	private double getStateLabel(MyState state) throws GoalDefinitionException {
		return machine.getGoal(state.getState(), myRole)
				- machine.getGoal(state.getState(), oponentRole);
	}

	private Instances getClassifiedInstances(FeatureExtractor featureVector,
			List<LabeledExample> labeledExamples) {
		Instances classifiedExamples = new Instances(
				featureVector.getInstances());
		for (LabeledExample e : labeledExamples) {
			Instance example = featureVector.getValues(e.getState());
			example.setClassValue(e.getValue());
			classifiedExamples.add(example);
		}
		return classifiedExamples;
	}

	private static final class LabeledExample {

		private MyState state;
		private double value;

		public LabeledExample(MyState state, double value) {
			this.state = state;
			this.value = value;
		}

		public MyState getState() {
			return state;
		}

		public double getValue() {
			return value;
		}
	}
	
	

}
