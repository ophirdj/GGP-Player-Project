package wekaclassifier;

import weka.classifiers.lazy.IBk;

public final class WekaIBkClassifier extends IBk implements IWekaClassifier {

	private static final long serialVersionUID = -2305693425639686802L;

	
	@Override
	public String toString() {
		return "k-near-neighbour";
	}
}