package heuristics;

import weka.classifiers.Classifier;
import weka.core.Instances;

public interface ClassifierBuilder {

	Classifier buildClassifier(Instances classifiedExamples);
	
}
