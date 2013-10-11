package gamestatistics;

public class PlayerStatistics {

	
	private final String name;
	private final int numVictories;
	private final int numDefeats;
	private final int numTies;
	private final int totalScore;

	public PlayerStatistics(String name, int numVictories, int numDefeats, int numTies, int totalScore) {
		this.name = name;
		this.numVictories = numVictories;
		this.numDefeats = numDefeats;
		this.numTies = numTies;
		this.totalScore = totalScore;
	}

	public String getName() {
		return name;
	}

	public int getNumVictories() {
		return numVictories;
	}

	public int getNumDefeats() {
		return numDefeats;
	}

	public int getNumTies() {
		return numTies;
	}

	public int getTotalScore() {
		return totalScore;
	}
	
}
