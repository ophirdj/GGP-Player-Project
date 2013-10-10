package classifier.heuristic.blind;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.IStateLabeler;
import states.LabeledState;
import weka.classifiers.Classifier;
import classifier.ClassifierBuildingException;
import classifier.IClassifier;
import classifier.IClassifierFactory;

public class NoHeuristicClassifierFactory implements IClassifierFactory {

	@Override
	public IClassifier createClassifier(IStateLabeler labeler, String gameName,
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
