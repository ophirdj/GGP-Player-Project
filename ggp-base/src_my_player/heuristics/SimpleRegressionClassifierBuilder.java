package heuristics;

import debugging.Verbose;
import state.MyState;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class SimpleRegressionClassifierBuilder implements ClassifierBuilder {

	@Override
	public StateClassifier buildClassifier(Instances classifiedExamples,
			final HeuristicsFeatureExtractor featureExtractor)
			throws ClassifierBuildException {
		final LinearRegression linearRegressionClassifier = new LinearRegression();
		Verbose.printVerbose(classifiedExamples.toSummaryString(), Verbose.CLASSIFIER_BUILDER);
		Verbose.printVerbose(linearRegressionClassifier.toString(), Verbose.CLASSIFIER_BUILDER);
		try {
			linearRegressionClassifier.setDebug(Verbose.isVerbose(Verbose.CLASSIFIER_BUILDER));
			linearRegressionClassifier.buildClassifier(classifiedExamples);
			return new StateClassifier() {

				@Override
				public double classifyState(MyState state)
						throws ClassificationException {
					try {
						double value = linearRegressionClassifier
								.classifyInstance(featureExtractor
										.getFeatureValues(state));
						return value;
					} catch (Exception e) {
						e.printStackTrace();
						throw new ClassificationException();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassifierBuildException();
		}
	}

}
