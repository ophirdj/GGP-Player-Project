package statecompare;

import state.MyState;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class SimpleRegressionComparerBuilder implements ComparerBuilder {

	@Override
	public StateComparer buildComparer(Instances comparedExamples,
			final StateComparisonFeatureExtractor featureExtractor)
			throws ComparerBuildException {
		final LinearRegression linearRegressionClassifier = new LinearRegression();
		System.out.println(comparedExamples.toSummaryString());
		System.out.println(linearRegressionClassifier.toString());
		try {
			linearRegressionClassifier.setDebug(true);
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
