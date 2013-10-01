package statecompare;

import state.MyState;
import weka.core.Instance;
import weka.core.Instances;

public interface StateComparisonFeatureExtractor {

	Instance getFeatureValues(MyState state1, MyState state2);
	
	Instances getDatasetHeader();
	
}
