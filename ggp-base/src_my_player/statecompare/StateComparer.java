package statecompare;

import state.MyState;

public interface StateComparer {

	// XXX: maybe return just an int instead of double?
	double compare(MyState state1, MyState state2) throws ComparisonException;

	class ComparisonException extends heuristics.StateClassifier.ClassificationException {

		private static final long serialVersionUID = -1917385003466540203L;}
	
}
