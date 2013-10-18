package wekaclassifier;

import weka.classifiers.functions.MultilayerPerceptron;

public final class WekaNeuralNetworkClassifier extends MultilayerPerceptron implements
		IWekaClassifier {

	private static final long serialVersionUID = -5738599499628940141L;

	@Override
	public String toString() {
		return "Neural Network";
	}
}
