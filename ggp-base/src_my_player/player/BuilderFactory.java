package player;

import statecompare.ComparerBuilder;
import heuristics.ClassifierBuilder;

public interface BuilderFactory {

	ClassifierBuilder createClassifierBuilder();
	ComparerBuilder createComparerBuilder();
}
