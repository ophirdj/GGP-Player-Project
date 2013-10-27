package stateclassifier.heuristic.blind;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import stateclassifier.ClassifierBuildingException;
import stateclassifier.IStateClassifier;
import stateclassifier.IStateClassifierFactory;
import states.LabeledState;
import weka.classifiers.Classifier;

public class NoHeuristicClassifierFactory implements IStateClassifierFactory {

	@Override
	public IStateClassifier createClassifier(IStateLabeler labeler, String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier)
			throws ClassifierBuildingException {
		return new NoHeuristicClassifier(labeler);
	}
	
	@Override
	public String toString() {
		return NoHeuristicClassifier.class.getSimpleName();
	}

}
