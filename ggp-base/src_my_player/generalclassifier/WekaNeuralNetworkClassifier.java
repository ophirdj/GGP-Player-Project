package generalclassifier;

import weka.classifiers.functions.MultilayerPerceptron;

public class WekaNeuralNetworkClassifier extends MultilayerPerceptron implements
		IGeneralClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5738599499628940141L;

	@Override
	public String toString() {
		return "Neural Network";
	}
}
