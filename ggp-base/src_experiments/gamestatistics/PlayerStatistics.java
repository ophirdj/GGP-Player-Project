package gamestatistics;

public final class PlayerStatistics {

	public final String name;
	public final int numVictories;
	public final int numDefeats;
	public final int numTies;
	public final int totalScore;

	public PlayerStatistics(String name, int numVictories, int numDefeats,
			int numTies, int totalScore) {
		this.name = name;
		this.numVictories = numVictories;
		this.numDefeats = numDefeats;
		this.numTies = numTies;
		this.totalScore = totalScore;
	}

}
