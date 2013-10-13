package classifier.comparer.simple;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.LabeledState;
import weka.classifiers.Classifier;
import classifier.ClassifierBuildingException;
import classifier.IClassifier;
import classifier.IClassifierFactory;

public class SimpleComparerClassifierFactory implements IClassifierFactory {

	@Override
	public IClassifier createClassifier(IStateLabeler labeler, String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier)
			throws ClassifierBuildingException {
		return new SimpleComparerClassifier(gameName, contents, rules, labeledExamples, classifier);
	}

	@Override
	public String toString() {
		return SimpleComparerClassifier.class.getSimpleName();
	}
}
