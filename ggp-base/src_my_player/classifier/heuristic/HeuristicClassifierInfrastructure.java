package classifier.heuristic;

import states.MyState;
import utils.Verbose;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import classifier.ClassifierBuildingException;
import classifier.IClassifier;

public abstract class HeuristicClassifierInfrastructure implements IClassifier {
	
	private Classifier regressionClassifier;
	private boolean isTrained;
	
	public HeuristicClassifierInfrastructure(Classifier classifier) {
		this.regressionClassifier = classifier;
		this.isTrained = false;
	}
	
	protected void train(Instances data) throws ClassifierBuildingException{
		try {
			regressionClassifier.buildClassifier(data);
			this.isTrained = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassifierBuildingException();
		}
	}

	@Override
	public ClassifierValue getValue(MyState state)
			throws ClassificationException {
		assertTrained();
		try {
			return new DoubleValue(regressionClassifier.classifyInstance(stateToInstance(state)));
		} catch (Exception e) {
			Verbose.printVerboseError("Classification failed", Verbose.UNEXPECTED_VALUE);
			e.printStackTrace();
			throw new ClassificationException();
		}
	}
	

	@Override
	public boolean isBetterValue(ClassifierValue value1, ClassifierValue value2) throws ClassificationException {
		assertTrained();
		assertNumbers(value1, value2);
		return ((DoubleValue)value1).getValue() > ((DoubleValue)value2).getValue();
	}
	
	private void assertNumbers(ClassifierValue value1, ClassifierValue value2) throws ClassificationException {
		if(!(value1 instanceof DoubleValue && value2 instanceof DoubleValue)){
			Verbose.printVerboseError("Unexpected values", Verbose.UNEXPECTED_VALUE);
			throw new ClassificationException();
		}
	}

	private void assertTrained() throws ClassificationException{
		if(!isTrained){
			Verbose.printVerboseError("Untrained classifier", Verbose.UNEXPECTED_VALUE);
			throw new ClassificationException();
		}
	}
	
	protected abstract Instance stateToInstance(MyState state);
	
	public static class DoubleValue implements ClassifierValue{
		private Double value;
		
		public DoubleValue(Double value) {
			this.value = value;
		}
		
		public Double getValue(){
			return value;
		}
	}

}
