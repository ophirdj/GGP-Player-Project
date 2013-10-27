package generalclassifier;

import weka.classifiers.trees.M5P;

public class WekaMP5TreeClassifier extends M5P implements IGeneralClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2712094362645793609L;

	@Override
	public String toString() {
		return "MP5 trees";
	}
}
