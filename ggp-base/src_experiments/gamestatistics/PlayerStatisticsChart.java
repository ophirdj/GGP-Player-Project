package gamestatistics;

import gamestatistics.ResultExtractor.GameResult;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import utils.Verbose;

public class PlayerStatisticsChart {

	private static final int CHART_IMAGE_WIDTH = 680;
	private static final int CHART_IMAGE_HEIGHT = 420;
	private static final String VICTORIES = "Victories";
	private static final String DEFEATS = "Defeats";
	private static final String TIES = "Ties";

	public final String name;
	private DefaultPieDataset dataset;
	private JFreeChart chart;
	public final ChartPanel chartPanel;

	public PlayerStatisticsChart(PlayerStatistics playerStats) {
		name = playerStats.name;
		dataset = new DefaultPieDataset();
		dataset.setValue(VICTORIES, playerStats.numVictories);
		dataset.setValue(DEFEATS, playerStats.numDefeats);
		dataset.setValue(TIES, playerStats.numTies);
		chart = ChartFactory.createPieChart(name, dataset, true, true, false);
		chartPanel = new ChartPanel(chart);
		
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSimpleLabels(true);
		plot.setIgnoreZeroValues(true);
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
	            "{1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);
	}

	public void update(GameResult gameResult) {
		switch (gameResult) {
		case WIN:
			dataset.setValue(VICTORIES,
					dataset.getValue(VICTORIES).intValue() + 1);
			break;
		case LOSE:
			dataset.setValue(DEFEATS, dataset.getValue(DEFEATS).intValue() + 1);
			break;
		case TIE:
			dataset.setValue(TIES, dataset.getValue(TIES).intValue() + 1);
			break;
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
