package simulator.iterative;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import labeler.IStateLabeler;
import stateclassifier.ClassifierBuildingException;
import stateclassifier.IStateClassifier;
import stateclassifier.heuristic.infrastructure.HeuristicClassifierInfrastructure;
import states.LabeledState;
import states.MyState;
import utils.BinaryValues;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public class IncrementalClassifer extends HeuristicClassifierInfrastructure implements IStateClassifier {

	private IBk classifier;
	private HashMap<Attribute, GdlSentence> attributeToSentence;
	private Instances dataset;

	public IncrementalClassifer(IStateLabeler labeler, IBk classifier,
			Set<GdlSentence> contents, String gameName,
			Collection<LabeledState> labeledExamples)
			throws ClassifierBuildingException {
		super(labeler, classifier);
		this.classifier = classifier;
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
	}
	
	private void fillDataset(Instances dataset,
			Collection<LabeledState> labeledExamples) {
		for (LabeledState example : labeledExamples) {
			Instance instance = stateToInstance(example.getState());
			instance.setClassValue(example.getValue());
			dataset.add(instance);
		}
	}
	
	public void addExample(LabeledState state) throws Exception{
		Instance example = stateToInstance(state.getState());
		example.setClassValue(state.getValue());
		classifier.updateClassifier(example);
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
