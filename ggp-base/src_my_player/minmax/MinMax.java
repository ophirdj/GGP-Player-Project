package minmax;

import heuristics.StateClassifier.ClassificationException;

import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import state.MyState;

public interface MinMax {

	Move bestMove(MyState state) throws MinMaxException, GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException, ClassificationException;

	void clear();
	
	class MinMaxException extends Exception {
		private static final long serialVersionUID = 2089399546677381050L;
		
		public MinMaxException() {
			super();
		}
		
		public MinMaxException(String message) {
			super(message);
		}
		
	};

}
