package wekaclassifier;

import weka.classifiers.meta.RegressionByDiscretization;

public class WekaRegressionByDiscretizationClassifier extends
		RegressionByDiscretization implements IWekaClassifier {

	private static final long serialVersionUID = -961632427182190901L;

	@Override
	public String toString() {
		
		return "Regression By Discretization";
	}
}
