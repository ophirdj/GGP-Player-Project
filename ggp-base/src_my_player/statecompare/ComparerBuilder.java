package statecompare;

import weka.core.Instances;

public interface ComparerBuilder {

	StateComparer buildComparer(Instances comparedExamples,
			StateComparisonFeatureExtractor featureExtractor)
			throws ComparerBuildException;

	class ComparerBuildException extends heuristics.ClassifierBuilder.ClassifierBuildException {
		private static final long serialVersionUID = 8538798209144589014L;
	}

}
