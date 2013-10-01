package heuristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.MyState;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;



public class ManualTicTacToeFeatureExtractor implements HeuristicsFeatureExtractor{
	
	private Instances datasetHeader;

	public ManualTicTacToeFeatureExtractor() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(2 + 1);
		List<String> binaryVals = new ArrayList<String>(2);
		binaryVals.add("TRUE");
		binaryVals.add("FALSE");
		Attribute attribute1 = new Attribute("x in center", binaryVals);
		attributes.add(attribute1);
		Attribute attribute2 = new Attribute("o in center", binaryVals);
		attributes.add(attribute2);
		Attribute classAttribute = new Attribute("state value");
		attributes.add(classAttribute);
		this.datasetHeader = new Instances("Tic Tac Toe", attributes, 0);
		datasetHeader.setClass(classAttribute);
	}

	@Override
	public Instance getFeatureValues(MyState state) {
		char board[][] = parseState(state.getContents());
		Instance featureVector = new DenseInstance(datasetHeader.numAttributes());
		featureVector.setDataset(datasetHeader);
		
		if(board[1][1] == 'x') {
			featureVector.setValue(0, "TRUE");
		} else {
			featureVector.setValue(0, "FALSE");
		}
		
		if(board[1][1] == 'o') {
			featureVector.setValue(1, "TRUE");
		} else {
			featureVector.setValue(1, "FALSE");
		}
		
		return featureVector;
	}

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

	@Override
	public Instances getDatasetHeader() {
		return datasetHeader;
	}

}
