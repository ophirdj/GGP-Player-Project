package heuristics;

import java.util.ArrayList;

import state.MyState;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class ManualTicTacToeFeatureVector implements FeatureVector {
	
	
	private Instances datasetHeader;

	public ManualTicTacToeFeatureVector() {
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(1 + 1);
		Attribute attribute1 = new Attribute("dumb attribute");
		attributes.add(attribute1);
		Attribute classAttribute = new Attribute("state value");
		attributes.add(classAttribute);
		this.datasetHeader = new Instances("Tic Tac Toe", attributes, 0);
		datasetHeader.setClass(classAttribute);
	}

	@Override
	public Instance getValues(MyState state) {
		Instance featureVector = new DenseInstance(datasetHeader.numAttributes());
		featureVector.setDataset(datasetHeader);
		featureVector.setValue(0, 0);
		return featureVector;
	}

	@Override
	public Instances getInstances() {
		return datasetHeader;
	}

}
