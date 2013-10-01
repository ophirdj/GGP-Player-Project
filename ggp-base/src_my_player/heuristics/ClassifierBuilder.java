package heuristics;

import weka.core.Instances;

public interface ClassifierBuilder {

	StateClassifier buildClassifier(Instances classifiedExamples, HeuristicsFeatureExtractor featureExtractor)
			throws ClassifierBuildException;

	class ClassifierBuildException extends Exception {
		private static final long serialVersionUID = 6530642303159055786L;
	}

}
