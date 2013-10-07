package interfaces;

import org.ggp.base.util.observer.Subject;

import weka.core.Instances;

/**
 * Interface for classifier factory.
 * 
 * 
 * @author Ophir De Jager
 * 
 */
public interface IClassifierFactory extends Subject {

	/**
	 * Build a new classifier from the data set capable of classifying new
	 * states.
	 * 
	 * @param dataset
	 *            Labeled instances.
	 * @param featureExtractor
	 *            Feature extractor.
	 * @param labeler
	 *            State Labeler.
	 * @return A new classifier from data set.
	 */
	IClassifier buildClassifier(Instances dataset,
			IFeatureExtractor featureExtractor, IStateLabeler labeler);

}
