package stateclassifier.heuristic.blind;

import labeler.IStateLabeler;
import stateclassifier.IStateClassifier;
import stateclassifier.heuristic.infrastructure.HeuristicClassifierInfrastructure.DoubleValue;
import states.LabeledState;
import states.MyState;
import utils.Verbose;

public class NoHeuristicClassifier implements IStateClassifier {

	private IStateLabeler labeler;

	public NoHeuristicClassifier(IStateLabeler labeler) {
		this.labeler = labeler;
	}

	@Override
	public ClassifierValue getValue(MyState state)
			throws ClassificationException {
		try {
			LabeledState labeled = labeler.label(state);
			if (labeled != null) {
				return new DoubleValue(labeled.getValue());
			}
			return new DoubleValue(0.0);
		} catch (Exception e) {
			Verbose.printVerboseError("Classification failed",
					Verbose.UNEXPECTED_VALUE);
			e.printStackTrace();
			throw new ClassificationException();
		}
	}

	@Override
	public boolean isBetterValue(ClassifierValue value1, ClassifierValue value2)
			throws ClassificationException {
		assertNumbers(value1, value2);
		return ((DoubleValue) value1).getValue() > ((DoubleValue) value2)
				.getValue();
	}

	private void assertNumbers(ClassifierValue value1, ClassifierValue value2)
			throws ClassificationException {
		if (!(value1 instanceof DoubleValue && value2 instanceof DoubleValue)) {
			Verbose.printVerboseError("Unexpected values",
					Verbose.UNEXPECTED_VALUE);
			throw new ClassificationException();
		}
	}

}
