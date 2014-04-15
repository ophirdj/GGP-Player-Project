package wekaclassifier;

import weka.classifiers.functions.SMO;

public class WekaSMO extends SMO implements IWekaClassifier {

	private static final long serialVersionUID = 1736378105957141223L;

	@Override
	public String toString() {
		return "sequential minimal optimization (SMO)";
	}

}
