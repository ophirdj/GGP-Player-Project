package classifier.comparer.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import classifier.ClassifierBuildingException;
import classifier.comparer.ComparerClassifierInfrastructure;

public class SimpleComparerClassifier extends ComparerClassifierInfrastructure {

	private Map<Attribute, GdlSentence> state1AttributeToSentence;
	private Map<Attribute, GdlSentence> state2AttributeToSentence;
	private Instances dataset;

	public SimpleComparerClassifier(String gameName, Set<GdlSentence> contents,
			List<Gdl> rules, Collection<LabeledState> labeledExamples,
			Classifier classifier) throws ClassifierBuildingException {
		super(classifier);
		this.state1AttributeToSentence = new HashMap<Attribute, GdlSentence>(
				contents.size());
		this.state2AttributeToSentence = new HashMap<Attribute, GdlSentence>(
				contents.size());
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(
				(2 * contents.size()) + 1);
		for (GdlSentence sentence : contents) {
			Attribute attribute1 = new Attribute(sentence.toString()
					+ "(state 1)", BinaryValues.getValues());
			Attribute attribute2 = new Attribute(sentence.toString()
					+ "(state 2)", BinaryValues.getValues());
			state1AttributeToSentence.put(attribute1, sentence);
			state2AttributeToSentence.put(attribute2, sentence);
			attributes.add(attribute1);
			attributes.add(attribute2);
		}
		Attribute classAttribute = new Attribute("is better",
				BinaryValues.getValues());
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

	private void fillDataset(Instances dataset2,
			Collection<LabeledState> labeledExamples) {
		for (LabeledState example1 : labeledExamples) {
			for (LabeledState example2 : labeledExamples) {
				Instance instance = statesToInstance(example1.getState(),
						example2.getState());
				if (example1.getValue() > example2.getValue()) {
					instance.setClassValue(BinaryValues.TRUE_VALUE);
				} else {
					instance.setClassValue(BinaryValues.FALSE_VALUE);
				}
				dataset.add(instance);
			}
		}
	}

	@Override
	protected Instance statesToInstance(MyState state1, MyState state2) {
		Instance featureVector = new DenseInstance(dataset.numAttributes());
		featureVector.setDataset(dataset);
		calculateStateAttributeValues(state1, state1AttributeToSentence,
				featureVector);
		calculateStateAttributeValues(state2, state2AttributeToSentence,
				featureVector);
		return featureVector;
	}

	private void calculateStateAttributeValues(MyState state,
			Map<Attribute, GdlSentence> attributeToSentence,
			Instance featureVector) {
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
	}

}
