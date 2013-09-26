package heuristics;

import state.MyState;
import weka.core.Instance;
import weka.core.Instances;

public interface FeatureExtractor {
	
	Instance getValues(MyState state);
	
	Instances getInstances();
	
}
