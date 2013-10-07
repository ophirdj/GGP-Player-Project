package zivImplementation;

import interfaces.IClassifier;
import state.MyState;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import debugging.Verbose;

public abstract class ComparerClassifierInfrastructure implements IClassifier {

	private Classifier classificationClassifier;
	private boolean isTrained;
	
	public ComparerClassifierInfrastructure(Classifier classifier) {
		this.classificationClassifier = classifier;
		this.isTrained = false;
	}
	
	protected void train(Instances data) throws ClassifierBuildingException{
		try {
			classificationClassifier.buildClassifier(data);
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
		return new StateValue(state);
	}
	

	@Override
	public boolean isBetterValue(ClassifierValue value1, ClassifierValue value2) throws ClassificationException {
		assertTrained();
		assertStates(value1, value2);
		MyState firstValue = ((StateValue)value1).getValue();
		MyState secondValue = ((StateValue)value2).getValue();
		Instance example = statesToInstance(firstValue, secondValue);
		try {
			double classificationIndex = classificationClassifier.classifyInstance(example);
			String label = example.dataset().classAttribute().value((int) classificationIndex);
			return BinariesValues.getResult(label);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClassificationException();
		}
	}

	private void assertStates(ClassifierValue value1, ClassifierValue value2) throws ClassificationException {
		if(!(value1 instanceof StateValue && value2 instanceof StateValue)){
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
	
	protected abstract Instance statesToInstance(MyState state1, MyState state2);
	
	
	public static class StateValue implements ClassifierValue{
		private MyState value;
		
		public StateValue(MyState value) {
			this.value = value;
		}
		
		public MyState getValue(){
			return value;
		}
	}

}
