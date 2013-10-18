package gamestatistics;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.Verbose;

public final class ScoreChart {
	
	private static final int CHART_IMAGE_WIDTH = 680;
	private static final int CHART_IMAGE_HEIGHT = 420;

	private final DefaultCategoryDataset dataset;
	private final JFreeChart chart;
	private final ChartPanel chartPanel;
	private final Set<String> players;

	public ScoreChart() {
		dataset = new DefaultCategoryDataset();
		chart = ChartFactory.createBarChart("Total Score", "Players", "Score",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		chartPanel = new ChartPanel(chart);
		players = new HashSet<String>();
	}

	public ChartPanel getChart() {
		return chartPanel;
	}

	public void updateScores(List<String> playerNames, List<Integer> goals) {
		for (int i = 0; i < playerNames.size(); ++i) {
			String name = playerNames.get(i);
			int goal = goals.get(i);
			if (players.contains(name)) {
				dataset.incrementValue(goal, name, "");
			} else {
				players.add(name);
				dataset.addValue(goal, name, "");
			}
		}
	}

	public void save(String filename) {
		try {
			ChartUtilities.saveChartAsPNG(new File(filename), chart, CHART_IMAGE_WIDTH, CHART_IMAGE_HEIGHT);
		} catch (IOException e) {
			Verbose.printVerboseError("could not save to: " + filename, Verbose.FILE_IO);
		}
	}

}
