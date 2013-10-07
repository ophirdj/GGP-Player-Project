package interfaces;

import state.MyState;

/**
 * Interface for classifier that will be used in the minmax.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IClassifier {

	/**
	 * Classifies a state.
	 * 
	 * @param state
	 *            State to be classified.
	 * @return State classification.
	 */
	ClassifierValue getValue(MyState state) throws ClassificationException;

	/**
	 * Compare 2 state classifications and tell if the former is better than the
	 * later.
	 * 
	 * @param value1
	 *            Classification of 1st state.
	 * @param value2
	 *            Classification of 2nd state.
	 * @return True if (and only if) the 1st classification is better than the
	 *         2nd.
	 */
	boolean isBetterValue(ClassifierValue value1, ClassifierValue value2);

	/**
	 * Interface for classifications of the classifier
	 * 
	 * 
	 * @author Ophir De Jager
	 * 
	 */
	interface ClassifierValue {

	}

	class ClassificationException extends Exception {

		private static final long serialVersionUID = 1641500263812618199L;

	}

}
