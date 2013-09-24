package heuristics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ggp.base.util.gdl.grammar.GdlSentence;

import state.MyState;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * A class for converting game states to feature vectors
 * 
 * @author Ophir De Jager
 * 
 */
public class StateAnalyzer {

	protected static final int TRUE = 1;
	protected static final int FALSE = 0;

	protected static enum GeneralFeature {
		TURN_NUMBER;
	}

	protected final Map<GeneralFeature, Integer> generalFeaturesIndexes;
	protected final Map<GdlSentence, Integer> binaryFeaturesIndexes;

	public StateAnalyzer(Collection<GdlSentence> sentences) {
		generalFeaturesIndexes = getCrossGameFeaturesIndexes();
		binaryFeaturesIndexes = getBinaryFeaturesIndexes(sentences,
				generalFeaturesIndexes);
	}

	private final Map<GeneralFeature, Integer> getCrossGameFeaturesIndexes() {
		HashMap<GeneralFeature, Integer> crossGameFeaturesIndexes = new HashMap<GeneralFeature, Integer>(
				GeneralFeature.values().length);
		int index = 0;
		for (GeneralFeature feature : GeneralFeature.values()) {
			crossGameFeaturesIndexes.put(feature, index);
			++index;
		}
		return crossGameFeaturesIndexes;
	}

	private final Map<GdlSentence, Integer> getBinaryFeaturesIndexes(
			Collection<GdlSentence> sentences,
			final Map<GeneralFeature, Integer> generalFeaturesIndexes) {
		HashMap<GdlSentence, Integer> binaryFeaturesIndexes = new HashMap<GdlSentence, Integer>(
				sentences.size());
		int index = generalFeaturesIndexes.size();
		for (GdlSentence sentence : sentences) {
			binaryFeaturesIndexes.put(sentence, index);
			++index;
		}
		return binaryFeaturesIndexes;
	}

	public Instance getFeatureVector(MyState state) {
		final int numFeatures = generalFeaturesIndexes.size()
				+ binaryFeaturesIndexes.size();
		Instance featureVectorValues = new DenseInstance(numFeatures);

		// calculate cross-game features
		featureVectorValues.setValue(
				generalFeaturesIndexes.get(GeneralFeature.TURN_NUMBER),
				state.getTurnNumber());

		// calculate binary (GDL) features
		for (Map.Entry<GdlSentence, Integer> entry : binaryFeaturesIndexes
				.entrySet()) {
			featureVectorValues.setValue(entry.getValue(), FALSE);
		}
		for (GdlSentence sentence : state.getContents()) {
			Integer index = binaryFeaturesIndexes.get(sentence);
			if (index != null) {
				featureVectorValues.setValue(index, TRUE);
			}
		}

		return featureVectorValues;
	}

}
