package gamestatistics;

import gamestatistics.ResultExtractor.GameResult;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import logging.CSVGenerator;

import org.ggp.base.util.ui.table.JZebraTable;

import utils.Verbose;

public final class StatisticsTable {

	private static final String TOTAL_SCORE_COLUMN = "Total Score";
	private static final String GAMES_TIED_COLUMN = "Games Tied";
	private static final String GAMES_LOST_COLUMN = "Games Lost";
	private static final String GAMES_WON_COLUMN = "Games Won";
	private static final String PLAYER_COLUMN = "Player";
	private static final String headers[] = { PLAYER_COLUMN, GAMES_WON_COLUMN,
			GAMES_LOST_COLUMN, GAMES_TIED_COLUMN, TOTAL_SCORE_COLUMN };

	private final StatisticsPanel parentPanel;
	public final JTable statsTable;
	private final DefaultTableModel model;
	private final TableRowSorter<TableModel> sorter;

	public StatisticsTable(StatisticsPanel parent) {
		this.parentPanel = parent;
		model = new DefaultTableModel();
		for (String header : headers) {
			model.addColumn(header);
		}
		statsTable = new JZebraTable(model) {
			private static final long serialVersionUID = 6599141739431637522L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == model.findColumn(PLAYER_COLUMN))
					return String.class;
				if (colIndex == model.findColumn(GAMES_WON_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(GAMES_LOST_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(GAMES_TIED_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(TOTAL_SCORE_COLUMN))
					return Integer.class;
				return Object.class;
			}
		};
		statsTable.getColumnModel().getColumn(model.findColumn(PLAYER_COLUMN))
				.setPreferredWidth(2);
		statsTable.getColumnModel()
				.getColumn(model.findColumn(GAMES_WON_COLUMN))
				.setPreferredWidth(2);
		statsTable.getColumnModel()
				.getColumn(model.findColumn(GAMES_LOST_COLUMN))
				.setPreferredWidth(2);
		statsTable.getColumnModel()
				.getColumn(model.findColumn(GAMES_TIED_COLUMN))
				.setPreferredWidth(2);
		statsTable.getColumnModel()
				.getColumn(model.findColumn(TOTAL_SCORE_COLUMN))
				.setPreferredWidth(1);

		sorter = new TableRowSorter<TableModel>(model);
		sorter.setComparator(model.findColumn(GAMES_WON_COLUMN),
				new Comparator<Integer>() {

					@Override
					public int compare(Integer a, Integer b) {
						return a - b;
					}

				});
		sorter.setComparator(model.findColumn(GAMES_LOST_COLUMN),
				new Comparator<Integer>() {

					@Override
					public int compare(Integer a, Integer b) {
						return a - b;
					}

				});
		sorter.setComparator(model.findColumn(GAMES_TIED_COLUMN),
				new Comparator<Integer>() {

					@Override
					public int compare(Integer a, Integer b) {
						return a - b;
					}

				});
		sorter.setComparator(model.findColumn(TOTAL_SCORE_COLUMN),
				new Comparator<Integer>() {

					@Override
					public int compare(Integer a, Integer b) {
						return a - b;
					}

				});
		sorter.setSortKeys(Arrays.asList(new SortKey[] {
				new SortKey(model.findColumn(GAMES_WON_COLUMN),
						SortOrder.DESCENDING),
				new SortKey(model.findColumn(GAMES_LOST_COLUMN),
						SortOrder.DESCENDING),
				new SortKey(model.findColumn(GAMES_TIED_COLUMN),
						SortOrder.DESCENDING),
				new SortKey(model.findColumn(TOTAL_SCORE_COLUMN),
						SortOrder.DESCENDING) }));
		statsTable.setRowSorter(sorter);

		statsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = statsTable.getSelectedRow();
				if ((e.getClickCount() == 2) && (row >= 0)
						&& (row < statsTable.getRowCount())) {
					parentPanel.switchToChart(getRow(row));
				}
			}
		});
	}

	private PlayerStatistics getRow(int row) {
		return new PlayerStatistics((String) model.getValueAt(row,
				model.findColumn(PLAYER_COLUMN)), (Integer) model.getValueAt(
				row, model.findColumn(GAMES_WON_COLUMN)),
				(Integer) model.getValueAt(row,
						model.findColumn(GAMES_LOST_COLUMN)),
				(Integer) model.getValueAt(row,
						model.findColumn(GAMES_TIED_COLUMN)),
				(Integer) model.getValueAt(row,
						model.findColumn(TOTAL_SCORE_COLUMN)));
	}

	public void updateTable(List<String> players, List<Integer> goals,
			List<GameResult> results) {
		Set<String> playersToAdd = new HashSet<String>(players);
		updateTablePlayers(players, goals, results, playersToAdd);
		addNewPlayers(players, goals, results, playersToAdd);
		sorter.sort();
	}

	private void updateTablePlayers(List<String> players, List<Integer> goals,
			List<GameResult> results, Set<String> playersToAdd) {
		for (int tableRow = 0; tableRow < model.getRowCount(); ++tableRow) {
			String playerName = model.getValueAt(tableRow,
					model.findColumn(PLAYER_COLUMN)).toString();
			int playerIndex = players.indexOf(playerName);
			if (playerIndex >= 0) {
				model.setValueAt(
						(Integer) model.getValueAt(tableRow,
								model.findColumn(TOTAL_SCORE_COLUMN))
								+ goals.get(playerIndex), tableRow,
						model.findColumn(TOTAL_SCORE_COLUMN));
				switch (results.get(playerIndex)) {
				case WIN:
					model.setValueAt(
							(Integer) model.getValueAt(tableRow,
									model.findColumn(GAMES_WON_COLUMN)) + 1,
							tableRow, model.findColumn(GAMES_WON_COLUMN));
					break;
				case LOSE:
					model.setValueAt(
							(Integer) model.getValueAt(tableRow,
									model.findColumn(GAMES_LOST_COLUMN)) + 1,
							tableRow, model.findColumn(GAMES_LOST_COLUMN));
					break;
				case TIE:
					model.setValueAt(
							(Integer) model.getValueAt(tableRow,
									model.findColumn(GAMES_TIED_COLUMN)) + 1,
							tableRow, model.findColumn(GAMES_TIED_COLUMN));
					break;
				}
				playersToAdd.remove(playerName);
			}
		}
	}

	private void addNewPlayers(List<String> players, List<Integer> goals,
			List<GameResult> results, Set<String> playersToAdd) {
		for (String playerName : playersToAdd) {
			int playerIndex = players.indexOf(playerName);
			int numVictories = results.get(playerIndex) == GameResult.WIN ? 1
					: 0;
			int numDefeats = results.get(playerIndex) == GameResult.LOSE ? 1
					: 0;
			int numTies = results.get(playerIndex) == GameResult.TIE ? 1 : 0;
			Object row[] = new Object[headers.length];
			row[model.findColumn(PLAYER_COLUMN)] = playerName;
			row[model.findColumn(GAMES_WON_COLUMN)] = numVictories;
			row[model.findColumn(GAMES_LOST_COLUMN)] = numDefeats;
			row[model.findColumn(GAMES_TIED_COLUMN)] = numTies;
			row[model.findColumn(TOTAL_SCORE_COLUMN)] = goals.get(players
					.indexOf(playerName));
			model.addRow(row);
		}
	}

	public List<PlayerStatistics> getPlayersStats() {
		List<PlayerStatistics> stats = new ArrayList<PlayerStatistics>(
				statsTable.getRowCount());
		for (int i = 0; i < statsTable.getRowCount(); ++i) {
			stats.add(getRow(i));
		}
		return stats;
	}

	public void save(String directory, String filename) {
		CSVGenerator logger = null;
		try {
			logger = new CSVGenerator(directory, filename, headers);
			for (int row = 0; row < model.getRowCount(); ++row) {
				Object rowData[] = new Object[model.getColumnCount()];
				for (int column = 0; column < rowData.length; ++column) {
					rowData[column] = model.getValueAt(row, column);
				}
				logger.addRow(rowData);
			}
		} catch (IOException e) {
			Verbose.printVerboseError("could not save to: " + filename,
					Verbose.FILE_IO);
		} finally {
			if (logger != null) {
				try {
					logger.close();
				} catch (IOException e) {
					Verbose.printVerboseError("could not close logger",
							Verbose.FILE_IO);
				}
			}
		}
	}

}
