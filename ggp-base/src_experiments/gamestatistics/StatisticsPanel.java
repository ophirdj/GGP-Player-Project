package gamestatistics;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import logging.CSVGenerator;

import org.ggp.base.server.event.ServerMatchUpdatedEvent;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.ui.JLabelBold;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

@SuppressWarnings("serial")
public final class StatisticsPanel extends JPanel implements Observer
{
	
	private static final String headers[] = {"Player", "Games Won", "Games Lost", "Games Tied", "Total Score"};
	
	private final JTable statsTable;
	private final TableRowSorter<TableModel> sorter;
	private CSVGenerator logger;
	private Map<String, DefaultPieDataset> chartDatasets;
	private Map<String, JFreeChart> pieCharts;
	private Container chartContainer;
	private final JCheckBox showCharts;
	private File rootDirectory;
	
	public StatisticsPanel()
	{
		super(new BorderLayout());
		
		chartDatasets = new HashMap<String, DefaultPieDataset>();
		pieCharts = new HashMap<String, JFreeChart>();
		
        DefaultTableModel model = new DefaultTableModel();
        for(String header: headers) {
        	model.addColumn(header);
        }
		statsTable = new JTable(model)
		{
			@Override
			public boolean isCellEditable(int rowIndex, int colIndex)
			{
				return false;
			}
			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == 0) return String.class;
				if (colIndex == 1) return Integer.class;
				if (colIndex == 2) return Integer.class;
				if (colIndex == 3) return Integer.class;
				if (colIndex == 4) return Integer.class;
				return Object.class;
			}
		};
		statsTable.setShowHorizontalLines(true);
		statsTable.setShowVerticalLines(true);
		statsTable.getColumnModel().getColumn(1).setPreferredWidth(2);
		statsTable.getColumnModel().getColumn(2).setPreferredWidth(2);
		statsTable.getColumnModel().getColumn(3).setPreferredWidth(1);
		sorter = new TableRowSorter<TableModel>(model);
		sorter.setComparator(1, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a-b;
			}
		});
		sorter.setComparator(2, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a-b;
			}
		});
		sorter.setComparator(3, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a-b;
			}
		});
		sorter.setComparator(4, new Comparator<Integer>() {
			public int compare(Integer a, Integer b) {
				return a-b;
			}
		});
		sorter.setSortKeys(Arrays.asList(new SortKey[]{new SortKey(1, SortOrder.DESCENDING), new SortKey(2, SortOrder.DESCENDING), new SortKey(3, SortOrder.DESCENDING), new SortKey(4, SortOrder.DESCENDING)}));
		statsTable.setRowSorter(sorter);

		JPanel topContainer = new JPanel(new BorderLayout(3, 12));
		chartContainer = new JPanel(new GridLayout(0, 1));
		JScrollPane tableContainer = new JScrollPane(statsTable);
		statsTable.setPreferredScrollableViewportSize(new Dimension(400, 32));
		topContainer.add(tableContainer, BorderLayout.NORTH);
		topContainer.add(new JScrollPane(chartContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		chartContainer.setPreferredSize(new Dimension(tableContainer.getPreferredSize().width, 500));
		
		add(new JLabelBold("Statistics"), BorderLayout.NORTH);
		add(topContainer, BorderLayout.CENTER);
		
		showCharts = new JCheckBox("show charts?", true);
		showCharts.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chartContainer.setVisible(showCharts.isSelected());
			}
		});
		add(showCharts, BorderLayout.SOUTH);
		chartContainer.setVisible(showCharts.isSelected());
	}

	public void observe(Event event)
	{
		if (!(event instanceof ServerMatchUpdatedEvent)) return;
		Match match = ((ServerMatchUpdatedEvent) event).getMatch();
		
		if (!match.isCompleted()) return;
		if (match.getMatchId().startsWith("Test")) return;
		
		List<Integer> goals = match.getGoalValues();
		List<String> players = match.getPlayerNamesFromHost();
		for (int i = 0; i < players.size(); i++) { if (players.get(i)==null) { players.set(i, "?"); } }

		Set<String> playersToAdd = new HashSet<String>(players);
		DefaultTableModel model = (DefaultTableModel) statsTable.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			String rowPlayer = model.getValueAt(i, 0).toString();
			int playerIndex = players.indexOf(rowPlayer);
			if (playerIndex != -1) {
				DefaultPieDataset dataset = chartDatasets.get(rowPlayer);
				int numVictories = (Integer)model.getValueAt(i, 1);
				int numDefeats = (Integer)model.getValueAt(i, 2);
				int numTies = (Integer)model.getValueAt(i, 3);
				int oldScore = (Integer)model.getValueAt(i, 4);
				model.setValueAt(oldScore + goals.get(playerIndex), i, 4);
				if(isVictory(goals.get(playerIndex), goals)) {
					model.setValueAt(numVictories + 1, i, 1);
					dataset.setValue("Games Won", numVictories + 1);
				}
				if (isDefeat(goals.get(playerIndex), goals)) {
					model.setValueAt(numDefeats + 1, i, 2);
					dataset.setValue("Games Lost", numDefeats + 1);
				}
				if(isTie(goals)) {
					model.setValueAt(numTies + 1, i, 3);
					dataset.setValue("Games Tied", numTies + 1);
				}
				playersToAdd.remove(rowPlayer);
			}
		}
		for (String playerToAdd : playersToAdd) {
			int playerIndex = players.indexOf(playerToAdd);
			int numVictories = isVictory(goals.get(playerIndex), goals) ? 1 : 0;
			int numDefeats = isDefeat(goals.get(playerIndex), goals) ? 1 : 0;
			int numTies = isTie(goals) ? 1 : 0;
			model.addRow(new Object[]{playerToAdd, numVictories, numDefeats, numTies, goals.get(players.indexOf(playerToAdd))});
			
	        DefaultPieDataset dataset = new DefaultPieDataset();
	        dataset.setValue("Games Won", numVictories);
	        dataset.setValue("Games Lost", numDefeats);
	        dataset.setValue("Games Tied", numTies);
	        JFreeChart chart = ChartFactory.createPieChart(playerToAdd, dataset, true, false, false);
	        ChartPanel panel = new ChartPanel(chart);
	        chartContainer.add(panel);
	        chartDatasets.put(playerToAdd, dataset);
	        pieCharts.put(playerToAdd, chart);
		}
		sorter.sort();
	}

	private static boolean isVictory(Integer score, List<Integer> goals) {
		for(Integer goal: goals) {
			if(goal > score) {
				return false;
			}
		}
		return !isTie(goals);
	}
	
	private static boolean isDefeat(Integer score, List<Integer> goals) {
		for(Integer goal: goals) {
			if(goal < score) {
				return false;
			}
		}
		return !isTie(goals);
	}
	
	private static boolean isTie(List<Integer> goals) {
		for(Integer goal: goals) {
			if(goal != goals.get(0)) {
				return false;
			}
		}
		return true;
	}

	public void startSavingToDirectory(File rootDirectory) {
		try {
			this.rootDirectory = rootDirectory;
			File csv = new File(rootDirectory, "table.csv");
			logger = new CSVGenerator(csv.getAbsolutePath(), headers);
		} catch (IOException e) {
			logger = null;
		}
	}

	public void saveWhenNecessary() {
		if(logger != null) {
			try {
			DefaultTableModel model = (DefaultTableModel) statsTable.getModel();
			for(int row = 0; row < model.getRowCount(); ++row) {
				Object rowData[] = new Object[model.getColumnCount()];
				for(int column = 0; column < rowData.length; ++column) {
					rowData[column] = model.getValueAt(row, column);
				}
					logger.addRow(rowData);
			}
			logger.close();
			for(Entry<String, JFreeChart> entry: pieCharts.entrySet()) {
				File chartFile = new File(rootDirectory, entry.getKey() + ".png");
				ChartUtilities.saveChartAsPNG(chartFile, entry.getValue(), 680, 420);
			}
			} catch (IOException e) {
			} finally {
				logger = null;
			}
		}
	}
}
