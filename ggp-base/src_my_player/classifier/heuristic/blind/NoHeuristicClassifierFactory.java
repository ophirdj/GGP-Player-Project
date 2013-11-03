package classifier.heuristic.blind;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.LabeledState;
import weka.classifiers.Classifier;
import classifier.IClassifier;
import classifier.IHeuristicClassifierFactory;

public final class NoHeuristicClassifierFactory implements
		IHeuristicClassifierFactory {

	@Override
	public IClassifier createClassifier(IStateLabeler labeler, String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier) {
		return new NoHeuristicClassifier(labeler);
	}

	@Override
	public String toString() {
		return NoHeuristicClassifier.class.getSimpleName();
	}

}
