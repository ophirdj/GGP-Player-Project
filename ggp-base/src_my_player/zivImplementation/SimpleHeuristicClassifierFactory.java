package zivImplementation;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.LabeledState;
import weka.classifiers.Classifier;
import interfaces.IClassifier;
import interfaces.IClassifierFactory;

public class SimpleHeuristicClassifierFactory implements IClassifierFactory {

	@Override
	public IClassifier createClassifier(String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier)
			throws ClassifierBuildingException {
		return new SimpleHeuristicClassifier(gameName, contents, rules, labeledExamples, classifier);
	}

	
	@Override
	public String toString() {
		return SimpleHeuristicClassifier.class.getSimpleName();
	}
}
