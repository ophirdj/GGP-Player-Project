package statecompare;

import state.MyState;
import heuristics.StateClassifier.ClassificationException;

public interface StateComparer {

	// XXX: maybe return just an int instead of double?
	double compare(MyState state1, MyState state2) throws ClassificationException;

}
