package generalclassifier;

import weka.classifiers.functions.LinearRegression;

public class WekaLinearRegressionClassifier extends LinearRegression implements
		IGeneralClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -613990488067248260L;
	
	@Override
	public String toString() {
		return "Linear Regression";
	}
}
