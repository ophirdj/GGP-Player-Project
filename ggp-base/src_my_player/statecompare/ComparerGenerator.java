package statecompare;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import simulator.MapValueSimulator;
import state.MyState;
import statecompare.ComparerBuilder.ComparerBuildException;
import weka.core.Instance;
import weka.core.Instances;
import debugging.Verbose;

public class ComparerGenerator {

	private List<Gdl> rules;
	private String gameName;
	private StateMachine machine;
	private MapValueSimulator simulator;
	private StateComparisonFeatureExtractor featureExtractor;
	private ComparerBuilder comparerBuilder;

	public ComparerGenerator(Game game, StateMachine machine,
			MapValueSimulator simulator, ComparerBuilder comparerBuilder) {
		this.rules = game.getRules();
		this.gameName = game.getName();
		this.machine = machine;
		this.simulator = simulator;
		this.featureExtractor = null;
		this.comparerBuilder = comparerBuilder;
	}

	public StateComparer generateComparer(int numExamples)
			throws ComparerBuildException, MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		List<LabeledExample> labeledStates = new ArrayList<LabeledExample>(
				numExamples);
		Set<MyState> allStates = new HashSet<MyState>();
		Verbose.printVerbose("SIMULATIONS START",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		
		for (int i = 0; i < numExamples; i++) {
			List<MyState> simulation = simulator.simulate(machine.getRoles()
					.get(0));
			allStates.addAll(simulation);
		}
		
		Map<MyState, Integer> knownStates = simulator.getStateValueMap();
		Verbose.printVerbose("num of examples found: " + knownStates.keySet().size(), Verbose.SIMULATOR_MIN_MAX);
		for (MyState state : knownStates.keySet()){
			labeledStates.add(new LabeledExample(state, knownStates.get(state)));
		}
		
		
		Verbose.printVerbose("SIMULATIONS END",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Verbose.printVerbose("FEATURE EXTRACTION START",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		featureExtractor = new AutomaticStateComparisonFeatureExtractor(
				gameName, rules, allStates);
		Verbose.printVerbose("FEATURE EXTRACTION END",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Verbose.printVerbose("CLASSIFY INSTANCES START",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		Instances classifiedInstances = getClassifiedInstances(
				featureExtractor, labeledStates);
		Verbose.printVerbose("CLASSIFY INSTANCES END",
				Verbose.HEURISTIC_GENERATOR_VERBOSE);
		return comparerBuilder.buildComparer(classifiedInstances,
				featureExtractor);
	}

	private Instances getClassifiedInstances(
			StateComparisonFeatureExtractor featureExtractor2,
			List<LabeledExample> labeledExamples) {
		Instances classifiedExamples = new Instances(
				featureExtractor2.getDatasetHeader());
		for (LabeledExample e1 : labeledExamples) {
			for (LabeledExample e2 : labeledExamples) {
				if (e1 == e2) {
					continue;
				}
				Instance example = featureExtractor2.getFeatureValues(
						e1.getState(), e2.getState());
				example.setClassValue(e1.getValue() - e2.getValue());
				classifiedExamples.add(example);
			}
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
