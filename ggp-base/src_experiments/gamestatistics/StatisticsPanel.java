package gamestatistics;

import gamestatistics.ResultExtractor.GameResult;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.ggp.base.server.event.ServerMatchUpdatedEvent;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.ui.CloseableTabs;
import org.ggp.base.util.ui.JLabelBold;

import utils.Verbose;

public final class StatisticsPanel extends JPanel implements Observer {

	private static final long serialVersionUID = 4020968909765268404L;

	private static final String SCORES_FILENAME = "scores.png";
	private static final String TABLE_FILENAME = "statistics table.csv";

	private JTabbedPane tabsPane;
	private StatisticsTable table;
	private ScoreChart scores;
	private List<PlayerStatisticsChart> playersCharts;
	private File sessionDir;

	public StatisticsPanel() {
		super(new BorderLayout());

		playersCharts = new ArrayList<PlayerStatisticsChart>();

		add(new JLabelBold("General Statistics"), BorderLayout.NORTH);

		tabsPane = new JTabbedPane();

		table = new StatisticsTable(this);
		tabsPane.addTab("Summary",
				new JScrollPane(table.getTableView()));
		
		scores = new ScoreChart();
		tabsPane.addTab("High Scores", scores.getChart());

		add(tabsPane, BorderLayout.CENTER);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 600);
	}

	@Override
	public void observe(Event event) {
		if (!(event instanceof ServerMatchUpdatedEvent))
			return;
		Match match = ((ServerMatchUpdatedEvent) event).getMatch();

		if (!match.isCompleted()) {
			return;
		}
		if (match.getMatchId().startsWith("Test")) {
			return;
		}

		List<String> players = match.getPlayerNamesFromHost();
		for (int i = 0; i < players.size(); ++i) {
			if (players.get(i) == null) {
				Verbose.printVerboseError(
						"Statistics Panel: received null player name",
						Verbose.UNEXPECTED_VALUE);
				players.set(i, "?");
			}
		}
		List<Integer> goals = match.getGoalValues();
		List<GameResult> results = ResultExtractor.getGameResults(goals);

		table.updateTable(players, goals, results);
		scores.updateScores(players, goals);
		for (PlayerStatisticsChart playerChart : playersCharts) {
			int playerIndex = players.indexOf(playerChart.getName());
			if (playerIndex >= 0) {
				playerChart.update(results.get(playerIndex));
			}
		}
	}

	public void startSavingToDirectory(File sessionDir) {
		this.sessionDir = sessionDir;
	}

	public void saveWhenNecessary() {
		if (sessionDir != null) {
			File tableFile = new File(sessionDir, TABLE_FILENAME);
			table.save(tableFile.getAbsolutePath());
			File scoreFile = new File(sessionDir, SCORES_FILENAME);
			scores.save(scoreFile.getAbsolutePath());
			for (PlayerStatistics playerStats : table.getPlayersStats()) {
				File playerFile = new File(sessionDir, playerStats.getName()
						+ ".png");
				(new PlayerStatisticsChart(playerStats)).save(playerFile
						.getAbsolutePath());
			}
		}
	}

	public void switchToChart(PlayerStatistics playerStats) {
		for (int i = 0; i < tabsPane.getTabCount(); ++i) {
			String tabName = tabsPane.getTitleAt(i);
			if (tabName != null && tabName.equals(playerStats.getName())) {
				tabsPane.setSelectedIndex(i);
				return;
			}
		}
		PlayerStatisticsChart playerChart = new PlayerStatisticsChart(
				playerStats);
		playersCharts.add(playerChart);
		CloseableTabs.addClosableTab(tabsPane, playerChart.getChart(),
				playerChart.getName(), addTabCloseButton(playerChart));
		tabsPane.setSelectedIndex(tabsPane.getTabCount() - 1);
	}

	private AbstractAction addTabCloseButton(
			final PlayerStatisticsChart playerChart) {
		return new AbstractAction("x") {

			private static final long serialVersionUID = 846256002308622567L;

			public void actionPerformed(ActionEvent evt) {
				tabsPane.remove(playerChart.getChart());
				playersCharts.remove(playerChart);
			}
		};
	}

}
