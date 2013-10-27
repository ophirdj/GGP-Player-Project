package simulator.iterative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import labeler.IStateLabeler;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import simulator.BaseSimulator;
import simulator.ISimulator;
import stateclassifier.ClassifierBuildingException;
import stateclassifier.IStateClassifier.ClassificationException;
import stateclassifier.IStateClassifier.ClassifierValue;
import states.LabeledState;
import states.MyState;
import weka.classifiers.lazy.IBk;

public class IterativeSimulator extends BaseSimulator implements ISimulator {

	private static final int BASIC_TRAINING_SIMULATION = 20;

	private static final String GAME_NAME = "IterativeSimulator";

	private Role maxplayer;
	private HashMap<MachineState, LabeledState> labeled;
	private IncrementalClassifer incrementalClassifier;
	private int simulationAmount;

	public IterativeSimulator(StateMachine machine, IStateLabeler labeler,
			Role maxplayer) {
		super(machine, labeler);
		this.maxplayer = maxplayer;
		this.labeled = new HashMap<MachineState, LabeledState>();
		this.simulationAmount = 0;
	}

	@Override
	public void Simulate(MyState rootState) throws MoveDefinitionException,
			TransitionDefinitionException, GoalDefinitionException {
		if (simulationAmount < BASIC_TRAINING_SIMULATION) {
			simulationAmount++;
			trainingSimulation(rootState);
			if (simulationAmount == BASIC_TRAINING_SIMULATION) {
				buildGuidingClassifer();
			}
		} else if (incrementalClassifier == null) {
			trainingSimulation(rootState);
		} else {
			try {
				guidedSimulation(rootState);
			} catch (Exception e) {
				e.printStackTrace();
				trainingSimulation(rootState);
			}
		}
	}

	private void buildGuidingClassifer() {
		try {
			incrementalClassifier = new IncrementalClassifer(labeler,
					new IBk(), getAllContents(), GAME_NAME, labeled.values());
		} catch (ClassifierBuildingException e) {
			e.printStackTrace();
			incrementalClassifier = null;
		}
	}

	private void guidedSimulation(MyState rootState)
			throws Exception {
		List<MyState> simulation = new ArrayList<MyState>();
		MyState state = rootState;
		while (!machine.isTerminal(state.getState())) {
			simulation.add(state);
			addContents(state);
			List<MachineState> nextStates = machine.getNextStates(state.getState());
			state = getBestNextState(nextStates, state);
		}
		simulation.add(state);
		addContents(state);
		MyState finalState = simulation.get(simulation.size() - 1);
		LabeledState finalLabeled = labeler.label(finalState);
		labeled.put(finalState.getState(), finalLabeled);
		incrementalClassifier.addExample(finalLabeled);
	}

	private MyState getBestNextState(List<MachineState> nextStates, MyState rootState) throws ClassificationException {
		if(nextStates.isEmpty()){
			return null;
		}
		MyState maxState = MyState.createChild(rootState, nextStates.remove(0));
		ClassifierValue maxValue = incrementalClassifier.getValue(maxState);
		while(!nextStates.isEmpty()){
			MyState currentState = MyState.createChild(rootState, nextStates.remove(0));
			ClassifierValue currentValue = incrementalClassifier.getValue(maxState);
			
			if(betterValue(currentValue, maxValue, rootState.getRole())){
				maxState = currentState;
				maxValue = currentValue;
			}
		}
		return maxState;
	}
	
	

	private boolean betterValue(ClassifierValue currentValue,
			ClassifierValue maxValue, Role role) throws ClassificationException {
		if (currentValue == null) {
			return false;
		} else if (maxValue == null) {
			return true;
		} else if (role == maxplayer) {
			return incrementalClassifier.isBetterValue(currentValue, maxValue);
		} else {
			return incrementalClassifier.isBetterValue(maxValue, currentValue);
		}
	}

	private void trainingSimulation(MyState rootState)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		List<MyState> simulation = new ArrayList<MyState>();
		MyState state = rootState;
		while (!machine.isTerminal(state.getState())) {
			simulation.add(state);
			addContents(state);
			MachineState nextState = machine.getRandomNextState(state
					.getState());
			state = MyState.createChild(state, nextState);
		}
		simulation.add(state);
		addContents(state);
		MyState finalState = simulation.get(simulation.size() - 1);
		labeled.put(finalState.getState(), labeler.label(finalState));
	}

	@Override
	public Collection<LabeledState> getLabeledStates() {
		return labeled.values();
	}

}
