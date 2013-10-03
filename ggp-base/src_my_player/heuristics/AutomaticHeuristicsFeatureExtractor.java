package heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.MyState;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ProtectedProperties;

public class AutomaticHeuristicsFeatureExtractor implements HeuristicsFeatureExtractor {

	private static final List<String> binaryValues = getBinaryValues();

	private Instances datasetHeader;
	private Map<Attribute, GdlSentence> attributeToSentence;

	private static List<String> getBinaryValues() {
		List<String> binaryValues = new ArrayList<String>(2);
		binaryValues.add("FALSE");
		binaryValues.add("TRUE");
		return binaryValues;
	}

	public AutomaticHeuristicsFeatureExtractor(String gameName, List<Gdl> rules,
			Set<MyState> states) {
		Set<GdlSentence> sentences = new HashSet<GdlSentence>();
		for (MyState s : states) {
			sentences.addAll(s.getContents());
		}
		this.attributeToSentence = new HashMap<Attribute, GdlSentence>(
				sentences.size());
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(
				sentences.size() + 1);
		for (GdlSentence sentence : sentences) {
			Attribute attribute = new Attribute(sentence.toString(),
					binaryValues);
			attributeToSentence.put(attribute, sentence);
			attributes.add(attribute);
		}
		Properties props = new Properties();
		props.setProperty("range", "(-100, 100)");
		Attribute classAttribute = new Attribute("state value", new ProtectedProperties(props));
		attributes.add(classAttribute);
		datasetHeader = new Instances(gameName, attributes, 0);
		datasetHeader.setClass(classAttribute);
	}

	@Override
	public Instance getFeatureValues(MyState state) {
		Instance featureVector = new DenseInstance(
				datasetHeader.numAttributes());
		featureVector.setDataset(datasetHeader);

		Set<GdlSentence> sentences = state.getContents();
		for (Entry<Attribute, GdlSentence> entry : attributeToSentence
				.entrySet()) {
			if (sentences.contains(entry.getValue())) {
				featureVector.setValue(entry.getKey(), "TRUE");
			} else {
				featureVector.setValue(entry.getKey(), "FALSE");
			}
		}

//		System.out.println("STATE EVALUATION START");
//		for(Attribute attribute: attributeToSentence.keySet()) {
//			System.out.println(attribute.name() + ": " + featureVector.value(attribute));
//		}
//		printBoard(parseState(sentences));
//		System.out.println("STATE EVALUATION END");
//		System.out.println();
		
		return featureVector;
	}

	@Override
	public Instances getDatasetHeader() {
		return datasetHeader;
	}
	
	
	@SuppressWarnings("unused")
	private char[][] parseState(Set<GdlSentence> contents) {
		char board[][] = new char[3][3];
		for(GdlSentence s: contents) {
			String str = s.get(0).toString();
			if(str.contains("cell")) {
				int row = Integer.parseInt(str.substring(7, 8));
				int col = Integer.parseInt(str.substring(9, 10));
				char type = str.charAt(11);
				board[row-1][col-1] = type;
			}
		}
		return board;
	}

	@SuppressWarnings("unused")
	private void printBoard(char[][] board) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				System.out.print("" + board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	

}
