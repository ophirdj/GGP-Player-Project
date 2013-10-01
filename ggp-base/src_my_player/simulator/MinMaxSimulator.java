package simulator;

import java.util.List;
import java.util.Set;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import debugging.Verbose;

import state.MyState;

public class MinMaxSimulator {

	private StateMachine machine;

	public MinMaxSimulator(StateMachine machine) {
		this.machine = machine;
	}

	public void AddStateToMyPlayer(MyState state, Set<MachineState> knownWin,
			Set<MachineState> knownLose, Role myRole, Role oponenetRole)
			throws MoveDefinitionException, TransitionDefinitionException,
			GoalDefinitionException {
		if (machine.isTerminal(state.getState())) {
			Verbose.printVerboseError("Simulator Min Max recieve final state", Verbose.UNEXPECTED_VALUE);
		}
		else if (machine.getLegalMoves(state.getState(), myRole).size() > 1) {
			AddStateToCurrentPlayer(state, knownWin, knownLose, myRole);
		}
		else{
			AddStateToCurrentPlayer(state, knownLose, knownWin, oponenetRole);
		}
	}

	private void AddStateToCurrentPlayer(MyState state,
			Set<MachineState> currentRoleWin,
			Set<MachineState> currentRoleLose, Role currentRole)
			throws GoalDefinitionException, MoveDefinitionException,
			TransitionDefinitionException {
		boolean foundMove = false;
		List<MachineState> nextStates = machine.getNextStates(state.getState());
		for (MachineState consideratedState : nextStates) {
			if (currentRoleWin.contains(consideratedState)) {
				currentRoleWin.add(state.getState());
				foundMove = true;
			} else if (currentRoleLose.contains(state.getState())) {
				continue;
			} else if (machine.isTerminal(state.getState())) {
				int goalValue = machine.getGoal(state.getState(), currentRole);
				if (goalValue > 50) {
					if(currentRoleWin.add(state.getState())){
						Verbose.printVerbose("Current State ", Verbose.SIMULATOR_MIN_MAX);
					}
					currentRoleWin.add(consideratedState);
					Verbose.printVerbose("Son state forced", Verbose.SIMULATOR_MIN_MAX);
					foundMove = true;
				} else if (goalValue < 50) {
					currentRoleLose.add(consideratedState);
					Verbose.printVerbose("Son state forced", Verbose.SIMULATOR_MIN_MAX);
					continue;
				} else {
					foundMove = true;
				}
			} else {
				foundMove = true;
			}
		}
		if (!foundMove) {
			currentRoleLose.add(state.getState());
			Verbose.printVerbose("Current state forced", Verbose.SIMULATOR_MIN_MAX);
		}
	}

}
