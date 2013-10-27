package generalclassifier;

import weka.classifiers.lazy.IBk;

public class WekaIBkClassifier extends IBk implements IGeneralClassifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2305693425639686802L;

	
	@Override
	public String toString() {
		return "k-near-neighbour";
	}
}
