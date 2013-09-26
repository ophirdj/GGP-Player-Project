package heuristics;

import state.MyState;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SimpleLinearRegression;
import weka.core.Instances;

public class SimpleRegressionClassifierBuilder implements ClassifierBuilder {

	@Override
	public StateClassifier buildClassifier(Instances classifiedExamples, final FeatureVector featureVector) throws ClassifierBuildException {
		final SimpleLinearRegression simpleLinearRegressionClassifier = new SimpleLinearRegression();
		System.out.println(classifiedExamples.toSummaryString());
		try {
			simpleLinearRegressionClassifier.setDebug(true);
			simpleLinearRegressionClassifier.buildClassifier(classifiedExamples);
			return new StateClassifier() {
				
				@Override
				public double classifyState(MyState state) throws ClassificationException {
					try {
						return simpleLinearRegressionClassifier.classifyInstance(featureVector.getValues(state));
					} catch (Exception e) {
						throw new ClassificationException();
					}
				}
			};
		} catch (Exception e) {
			throw new ClassifierBuildException();
		}
	}

}
