package heuristics;

import state.MyState;
import weka.core.Attribute;

public class Feature {

	private Attribute attribute;
	private AttributeType type;
	private FeatureFunction function;

	public Feature(Attribute attribute, AttributeType type,
			FeatureFunction function) {
		this.attribute = attribute;
		this.type = type;
		this.function = function;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public AttributeType getType() {
		return type;
	}

	public Object getFeatureValue(MyState state) {
		return function.getValue(state);
	}

}
