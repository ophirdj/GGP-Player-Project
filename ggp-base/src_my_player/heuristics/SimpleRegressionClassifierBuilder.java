package heuristics;

import state.MyState;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class SimpleRegressionClassifierBuilder implements ClassifierBuilder {

	@Override
	public StateClassifier buildClassifier(Instances classifiedExamples,
			final HeuristicsFeatureExtractor featureExtractor)
			throws ClassifierBuildException {
		final LinearRegression linearRegressionClassifier = new LinearRegression();
		System.out.println(classifiedExamples.toSummaryString());
		System.out.println(linearRegressionClassifier.toString());
		try {
			linearRegressionClassifier.setDebug(true);
			linearRegressionClassifier.buildClassifier(classifiedExamples);
			return new StateClassifier() {

				@Override
				public double classifyState(MyState state)
						throws ClassificationException {
					try {
						double value = linearRegressionClassifier
								.classifyInstance(featureExtractor
										.getFeatureValues(state));
//						 printTicTacToe(state.getContents());
//						 System.out.println(value);
//						 System.out.println();
//						 System.out.println();
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
