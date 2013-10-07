package interfaces;

import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.observer.Subject;

import state.LabeledState;
import weka.core.Instances;

/**
 * Interface for feature extraction from GDL and creation of data sets for
 * classifier building.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IFeatureExtractor extends Subject {

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
	Instances createDataset(Set<GdlSentence> contents, List<Gdl> rules, Set<LabeledState> dataset);

}
