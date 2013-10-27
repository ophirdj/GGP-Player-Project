package stateclassifier.heuristic.simple;

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

public class SimpleHeuristicClassifierFactory implements IStateClassifierFactory {

	@Override
	public IStateClassifier createClassifier(IStateLabeler labeler, String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier)
			throws ClassifierBuildingException {
		return new SimpleHeuristicClassifier(labeler ,gameName, contents, rules, labeledExamples, classifier);
	}

	
	@Override
	public String toString() {
		return SimpleHeuristicClassifier.class.getSimpleName();
	}
}
