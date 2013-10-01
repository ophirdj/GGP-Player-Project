package heuristics;

import state.MyState;

public interface StateClassifier {

	double classifyState(MyState state) throws ClassificationException;

	class ClassificationException extends Exception {
		private static final long serialVersionUID = -5866082566146317714L;
	}

}
