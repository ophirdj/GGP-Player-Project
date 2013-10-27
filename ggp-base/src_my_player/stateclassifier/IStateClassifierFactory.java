package stateclassifier;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.LabeledState;
import weka.classifiers.Classifier;

/**
 * @author ronen
 * 
 */
public interface IStateClassifierFactory {

	/**
	 * @param machine A labeler for the game.
	 * @param gameName The name of the game.
	 * @param contents The GDL sentences found in the game.
	 * @param rules The game's rules (GDL format).
	 * @param labeledExamples A collection of labeled examples.
	 * @param classifier The classifier used in learning.
	 * @return A new IClassifier
	 * @throws ClassifierBuildingException If the building failed.
	 */
	IStateClassifier createClassifier(IStateLabeler labeler, String gameName, Set<GdlSentence> contents,
			List<Gdl> rules, Collection<LabeledState> labeledExamples,
			Classifier classifier) throws ClassifierBuildingException;

}
