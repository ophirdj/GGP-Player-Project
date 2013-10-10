package zivImplementation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.LabeledState;
import state.MyState;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public class SimpleHeuristicClassifier extends
		HeuristicClassifierInfrastructure {

	private HashMap<Attribute, GdlSentence> attributeToSentence;
	private Instances dataset;

	public SimpleHeuristicClassifier(String gameName, Set<GdlSentence> contents, List<Gdl> rules,
			Collection<LabeledState> labeledExamples, Classifier classifier) throws ClassifierBuildingException {
		super(classifier);
		this.attributeToSentence = new HashMap<Attribute, GdlSentence>(
				contents.size());
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(
				contents.size() + 1);
		for (GdlSentence sentence : contents) {
			Attribute attribute = new Attribute(sentence.toString(),
					BinariesValues.getValues());
			attributeToSentence.put(attribute, sentence);
			attributes.add(attribute);
		}
		Properties props = new Properties();
		props.setProperty("range", "(-100, 100)");
		Attribute classAttribute = new Attribute("state value", new ProtectedProperties(props));
		attributes.add(classAttribute);
		this.dataset = new Instances(gameName, attributes, 0);
		dataset.setClass(classAttribute);
		fillDataset(dataset, labeledExamples);
		train(dataset);
	}

	private void fillDataset(Instances dataset,
			Collection<LabeledState> labeledExamples) {
		for(LabeledState example : labeledExamples){
			Instance instance = stateToInstance(example.getState());
			instance.setClassValue(example.getValue());
			dataset.add(instance);
		}
	}
	
	@Override
	protected Instance stateToInstance(MyState state) {
		Instance featureVector = new DenseInstance(
				dataset.numAttributes());
		featureVector.setDataset(dataset);

		Set<GdlSentence> sentences = state.getContents();
		for (Entry<Attribute, GdlSentence> entry : attributeToSentence
				.entrySet()) {
			if (sentences.contains(entry.getValue())) {
				featureVector.setValue(entry.getKey(), BinariesValues.TRUE_VALUE);
			} else {
				featureVector.setValue(entry.getKey(), BinariesValues.FALSE_VALUE);
			}
		}
		return featureVector;
	}

}
