package heuristics;

import java.util.ArrayList;
import java.util.List;

import state.MyState;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class AutomaticFeatureVector implements FeatureVector {
	
	
	private List<Feature> features;
	private Instances datasetHeader;

	public AutomaticFeatureVector(List<Feature> features, String gameName) {
		this.features = features;
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(features.size() + 1);
		for(Feature f: features) {
			attributes.add(f.getAttribute());
		}
		Attribute classAttribute = new Attribute("state value");
		attributes.add(classAttribute);
		datasetHeader = new Instances(gameName, attributes, 0);
		datasetHeader.setClass(classAttribute);
	}

	@Override
	public Instance getValues(MyState state) {
		Instance featureVector = new DenseInstance(datasetHeader.numAttributes());
		featureVector.setDataset(datasetHeader);
		for(Feature f: features){
			switch(f.getType()) {
			case BINARY:
				featureVector.setValue(f.getAttribute(), (String) f.getFeatureValue(state));
				break;
			case NUMERIC:
				featureVector.setValue(f.getAttribute(), (Double) f.getFeatureValue(state));
				break;
			default:
				break;
			}
		}
		return featureVector;
	}

	@Override
	public Instances getInstances() {
		return datasetHeader;
	}

}
