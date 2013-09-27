package heuristics;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.MyState;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class SimpleRegressionClassifierBuilder implements ClassifierBuilder {

	@Override
	public StateClassifier buildClassifier(Instances classifiedExamples, final FeatureExtractor featureVector) throws ClassifierBuildException {
		final LinearRegression linearRegressionClassifier = new LinearRegression();
		System.out.println(classifiedExamples.toSummaryString());
		System.out.println(linearRegressionClassifier.toString());
		try {
			linearRegressionClassifier.setDebug(true);
			linearRegressionClassifier.buildClassifier(classifiedExamples);
			return new StateClassifier() {
				
				@Override
				public double classifyState(MyState state) throws ClassificationException {
					try {
						double value = linearRegressionClassifier.classifyInstance(featureVector.getValues(state));
//						char board[][] = parseState(state.getContents());
//						printBoard(board);
//						System.out.println(value);
//						System.out.println();
//						System.out.println();
						return value;
					} catch (Exception e) {
						throw new ClassificationException();
					}
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassifierBuildException();
		}
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
