package statecompare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.MyState;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class AutomaticStateComparisonFeatureExtractor implements
		StateComparisonFeatureExtractor {
	
	private static final List<String> binaryValues = getBinaryValues();

	private Instances datasetHeader;
	private Map<Attribute, GdlSentence> state1AttributeToSentence;
	private Map<Attribute, GdlSentence> state2AttributeToSentence;

	private static List<String> getBinaryValues() {
		List<String> binaryValues = new ArrayList<String>(2);
		binaryValues.add("FALSE");
		binaryValues.add("TRUE");
		return binaryValues;
	}

	public AutomaticStateComparisonFeatureExtractor(String gameName,
			List<Gdl> rules, Set<MyState> states) {
		Set<GdlSentence> sentences = new HashSet<GdlSentence>();
		for (MyState s : states) {
			sentences.addAll(s.getContents());
		}
		this.state1AttributeToSentence = new HashMap<Attribute, GdlSentence>(
				sentences.size());
		this.state2AttributeToSentence = new HashMap<Attribute, GdlSentence>(
				sentences.size());
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(
				sentences.size() * 2 + 1);
		for (GdlSentence sentence : sentences) {
			Attribute attribute1 = new Attribute(sentence.toString() + "(1)",
					binaryValues);
			state1AttributeToSentence.put(attribute1, sentence);
			Attribute attribute2 = new Attribute(sentence.toString() + "(2)",
					binaryValues);
			state1AttributeToSentence.put(attribute2, sentence);
			attributes.add(attribute1);
			attributes.add(attribute2);
		}
		Attribute classAttribute = new Attribute("state comparison value");
		attributes.add(classAttribute);
		datasetHeader = new Instances(gameName, attributes, 0);
		datasetHeader.setClass(classAttribute);
	}

	@Override
	public Instance getFeatureValues(MyState state1, MyState state2) {
		Instance featureVector = new DenseInstance(
				datasetHeader.numAttributes());
		featureVector.setDataset(datasetHeader);

		Set<GdlSentence> sentences1 = state1.getContents();
		for (Entry<Attribute, GdlSentence> entry : state1AttributeToSentence
				.entrySet()) {
			if (sentences1.contains(entry.getValue())) {
				featureVector.setValue(entry.getKey(), "TRUE");
			} else {
				featureVector.setValue(entry.getKey(), "FALSE");
			}
		}
		Set<GdlSentence> sentences2 = state2.getContents();
		for (Entry<Attribute, GdlSentence> entry : state2AttributeToSentence
				.entrySet()) {
			if (sentences2.contains(entry.getValue())) {
				featureVector.setValue(entry.getKey(), "TRUE");
			} else {
				featureVector.setValue(entry.getKey(), "FALSE");
			}
		}
		
		return featureVector;
	}

	@Override
	public Instances getDatasetHeader() {
		return datasetHeader;
	}

}
