package interfaces;

import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.LabeledState;
import state.MyState;
import weka.core.Instances;

/**
 * Interface for classifier that will be used in the minmax.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IClassifier {

	/**
	 * Create data set of labeled examples for classifier building.
	 * 
	 * @param contents
	 *            A set of state contents.
	 * @param rules
	 *            Game Rules.
	 * @param dataset
	 *            A set of labeled states.
	 * @return A labeled data set.
	 */
	Instances createDataset(Set<GdlSentence> contents, List<Gdl> rules,
			Set<LabeledState> dataset);
	
	/**
	 * Interface for feature extraction from GDL and creation of data sets for
	 * classifier building.
	 * 
	 * Also used to classify a state.
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
	 * @throws ClassificationException 
	 */
	boolean isBetterValue(ClassifierValue value1, ClassifierValue value2) throws ClassificationException;

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
