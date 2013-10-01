package heuristics;

import state.MyState;
import weka.core.Instance;
import weka.core.Instances;

public interface HeuristicsFeatureExtractor {
	
	Instance getFeatureValues(MyState state);
	
	Instances getDatasetHeader();
	
}
