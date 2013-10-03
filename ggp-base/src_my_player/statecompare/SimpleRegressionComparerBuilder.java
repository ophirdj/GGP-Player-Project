package statecompare;

import debugging.Verbose;
import state.MyState;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class SimpleRegressionComparerBuilder implements ComparerBuilder {

	@Override
	public StateComparer buildComparer(Instances comparedExamples,
			final StateComparisonFeatureExtractor featureExtractor)
			throws ComparerBuildException {
		final LinearRegression linearRegressionClassifier = new LinearRegression();
		Verbose.printVerbose(comparedExamples.toSummaryString(), Verbose.CLASSIFIER_BUILDER);
		Verbose.printVerbose(linearRegressionClassifier.toString(), Verbose.CLASSIFIER_BUILDER);
		try {
			linearRegressionClassifier.setDebug(Verbose.isVerbose(Verbose.CLASSIFIER_BUILDER));
			linearRegressionClassifier.buildClassifier(comparedExamples);
			return new StateComparer() {

				@Override
				public double compare(MyState state1, MyState state2)
						throws ComparisonException {
					try {
						double value = linearRegressionClassifier
								.classifyInstance(featureExtractor
										.getFeatureValues(state1, state2));
						return value;
					} catch (Exception e) {
						e.printStackTrace();
						throw new ComparisonException();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComparerBuildException();
		}
	}

}
