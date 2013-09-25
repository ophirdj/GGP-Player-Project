package heuristics;

import state.MyState;
import weka.core.Instance;
import weka.core.Instances;

public interface FeatureVector {
	
	Instance getValues(MyState state);
	
	Instances getInstances();
	
}
