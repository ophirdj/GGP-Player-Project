package gamestatistics;

import java.util.ArrayList;
import java.util.List;

public final class ResultExtractor {
	
	public static enum GameResult {
		WIN,
		LOSE,
		TIE
	}
	
	private static final int WIN_THRESHOLD = 75;
	
	public static List<GameResult> getGameResults(List<Integer> goals) {
		List<GameResult> results = new ArrayList<GameResult>(goals.size());
		boolean wasTie = true;
        for (int nGoal: goals) {
            if (nGoal > WIN_THRESHOLD) {
                wasTie = false;
                results.add(GameResult.WIN);
            } else {
            	results.add(GameResult.LOSE);
            }
        }
        if (wasTie) {
            for (int i = 0; i < goals.size(); ++i) {
            	results.set(i, GameResult.TIE);
            }
        }
        return results;
	}

}
