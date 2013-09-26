package heuristics;

import weka.core.Instances;

public interface ClassifierBuilder {

	StateClassifier buildClassifier(Instances classifiedExamples, FeatureVector featureVector)
			throws ClassifierBuildException;

	class ClassifierBuildException extends Exception {
		private static final long serialVersionUID = 6530642303159055786L;
	}

}
