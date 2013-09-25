package heuristics;



public class ManualTicTacToeFeatureExtractor implements FeatureExtractor{

	@Override
	public FeatureVector getFeatures() {
		return new ManualTicTacToeFeatureVector();
	}

}
