package classifier.heuristic.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import labeler.IStateLabeler;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import states.LabeledState;
import states.MyState;
import utils.BinaryValues;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;
import classifier.ClassifierBuildingException;
import classifier.heuristic.infrastructure.HeuristicClassifierInfrastructure;

public class SimpleHeuristicClassifier extends
		HeuristicClassifierInfrastructure {

	private HashMap<Attribute, GdlSentence> attributeToSentence;
	private Instances dataset;

	public SimpleHeuristicClassifier(IStateLabeler labeler, String gameName,
			Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier)
			throws ClassifierBuildingException {
		super(labeler, classifier);
		this.attributeToSentence = new HashMap<Attribute, GdlSentence>(
				contents.size());
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(
				contents.size() + 1);
		for (GdlSentence sentence : contents) {
			Attribute attribute = new Attribute(sentence.toString(),
					BinaryValues.getValues());
			attributeToSentence.put(attribute, sentence);
			attributes.add(attribute);
		}
		Properties props = new Properties();
		props.setProperty("range",
				"(" + labeler.getMinValue() + ", " + labeler.getMaxValue()
						+ ")");
		Attribute classAttribute = new Attribute("state value",
				new ProtectedProperties(props));
		attributes.add(classAttribute);
		this.dataset = new Instances(gameName, attributes, 0);
		dataset.setClass(classAttribute);
		fillDataset(dataset, labeledExamples);
		train(dataset);
		// we don't want to save the examples after we train the classifier
		emptyDataset(dataset);
	}

	private void emptyDataset(Instances dataset) {
		dataset.delete();
	}

	private void fillDataset(Instances dataset,
			Collection<LabeledState> labeledExamples) {
		for (LabeledState example : labeledExamples) {
			Instance instance = stateToInstance(example.getState());
			instance.setClassValue(example.getValue());
			dataset.add(instance);
		}
	}

	@Override
	protected Instance stateToInstance(MyState state) {
		Instance featureVector = new DenseInstance(dataset.numAttributes());
		featureVector.setDataset(dataset);

		Set<GdlSentence> sentences = state.getContents();
		for (Entry<Attribute, GdlSentence> entry : attributeToSentence
				.entrySet()) {
			if (sentences.contains(entry.getValue())) {
				featureVector.setValue(entry.getKey(), BinaryValues.TRUE_VALUE);
			} else {
				featureVector
						.setValue(entry.getKey(), BinaryValues.FALSE_VALUE);
			}
		}
		return featureVector;
	}

}
