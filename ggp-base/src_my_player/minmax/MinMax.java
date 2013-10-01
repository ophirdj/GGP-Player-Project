package minmax;

import org.ggp.base.util.statemachine.Move;
import state.MyState;

public interface MinMax {

	public Move bestMove(MyState state) throws MinMaxException;

	class MinMaxException extends Exception {
		private static final long serialVersionUID = 2089399546677381050L;
	};

}
