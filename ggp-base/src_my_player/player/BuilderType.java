package player;

import statecompare.ComparerBuilder;
import statecompare.SimpleRegressionComparerBuilder;
import heuristics.ClassifierBuilder;
import heuristics.SimpleRegressionClassifierBuilder;

public enum BuilderType {

	SIMPLE{

		@Override
		public String toString() {
			return "basic type";
		}
		
		@Override
		public BuilderFactory getBuilderFactory() {
			return new BuilderFactory(){

				@Override
				public ClassifierBuilder createClassifierBuilder() {
					return new SimpleRegressionClassifierBuilder();
				}

				@Override
				public ComparerBuilder createComparerBuilder() {
					return new SimpleRegressionComparerBuilder();
				}
				
				@Override
				public String toString() {
					return "simple classifier/comparator builder";
				}
			}
			;
		}
		
	}
	;
	public abstract BuilderFactory getBuilderFactory();
}
