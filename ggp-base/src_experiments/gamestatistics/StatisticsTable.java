package gamestatistics;

import gamestatistics.ResultExtractor.GameResult;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import utils.Verbose;

public class StatisticsTable {

	private static final String TOTAL_SCORE_COLUMN = "Total Score";
	private static final String GAMES_TIED_COLUMN = "Games Tied";
	private static final String GAMES_LOST_COLUMN = "Games Lost";
	private static final String GAMES_WON_COLUMN = "Games Won";
	private static final String PLAYER_COLUMN = "Player";
	private static final String headers[] = { PLAYER_COLUMN, GAMES_WON_COLUMN,
			GAMES_LOST_COLUMN, GAMES_TIED_COLUMN, TOTAL_SCORE_COLUMN };

	private StatisticsPanel parentPanel;
	private final JTable statsTable;
	private final TableRowSorter<TableModel> sorter;

	private static int indexOf(String columnName) {
		for (int i = 0; i < headers.length; ++i) {
			if (headers[i] == columnName) {
				return i;
			}
		}
		return -1;
	}

	public StatisticsTable(StatisticsPanel parent) {
		super();
		this.parentPanel = parent;
		DefaultTableModel model = new DefaultTableModel();
		for (String header : headers) {
			model.addColumn(header);
		}
		statsTable = new JTable(model) {
			private static final long serialVersionUID = 6599141739431637522L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == indexOf(PLAYER_COLUMN))
					return String.class;
				if (colIndex == indexOf(GAMES_WON_COLUMN))
					return Integer.class;
				if (colIndex == indexOf(GAMES_LOST_COLUMN))
					return Integer.class;
				if (colIndex == indexOf(GAMES_LOST_COLUMN))
					return Integer.class;
				if (colIndex == indexOf(TOTAL_SCORE_COLUMN))
					return Integer.class;
				return Object.class;
			}
		};
		statsTable.setShowHorizontalLines(true);
		statsTable.setShowVerticalLines(true);
		statsTable.getColumnModel().getColumn(1).setPreferredWidth(2);
		statsTable.getColumnModel().getColumn(2).setPreferredWidth(2);
		statsTable.getColumnModel().getColumn(3).setPreferredWidth(1);
		sorter = new TableRowSorter<TableModel>(model);
		sorter.setComparator(indexOf(GAMES_WON_COLUMN),
				new Comparator<Integer>() {
					public int compare(Integer a, Integer b) {
						return a - b;
					}
				});
		sorter.setComparator(indexOf(GAMES_LOST_COLUMN),
				new Comparator<Integer>() {
					public int compare(Integer a, Integer b) {
						return a - b;
					}
				});
		sorter.setComparator(indexOf(GAMES_TIED_COLUMN),
				new Comparator<Integer>() {
					public int compare(Integer a, Integer b) {
						return a - b;
					}
				});
		sorter.setComparator(indexOf(TOTAL_SCORE_COLUMN),
				new Comparator<Integer>() {
					public int compare(Integer a, Integer b) {
						return a - b;
					}
				});
		sorter.setSortKeys(Arrays
				.asList(new SortKey[] {
						new SortKey(indexOf(GAMES_WON_COLUMN),
								SortOrder.DESCENDING),
						new SortKey(indexOf(GAMES_LOST_COLUMN),
								SortOrder.DESCENDING),
						new SortKey(indexOf(GAMES_TIED_COLUMN),
								SortOrder.DESCENDING),
						new SortKey(indexOf(TOTAL_SCORE_COLUMN),
								SortOrder.DESCENDING) }));
		statsTable.setRowSorter(sorter);

		statsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					parentPanel.switchToChart(getRow(statsTable
							.getSelectedRow()));
				}
			}
		});
	}

	private PlayerStatistics getRow(int row) {
		DefaultTableModel model = (DefaultTableModel) statsTable.getModel();
		return new PlayerStatistics((String) model.getValueAt(row,
				indexOf(PLAYER_COLUMN)), (Integer) model.getValueAt(row,
				indexOf(GAMES_WON_COLUMN)), (Integer) model.getValueAt(row,
				indexOf(GAMES_LOST_COLUMN)), (Integer) model.getValueAt(row,
				indexOf(GAMES_TIED_COLUMN)), (Integer) model.getValueAt(row,
				indexOf(TOTAL_SCORE_COLUMN)));
	}

	public void updateTable(List<String> players, List<Integer> goals,
			List<GameResult> results) {
		Set<String> playersToAdd = new HashSet<String>(players);
		DefaultTableModel model = (DefaultTableModel) statsTable.getModel();
		updateTablePlayers(players, goals, results, playersToAdd, model);
		addNewPlayers(players, goals, results, playersToAdd, model);
		sorter.sort();
	}

	private void updateTablePlayers(List<String> players, List<Integer> goals,
			List<GameResult> results, Set<String> playersToAdd,
			DefaultTableModel model) {
		for (int tableRow = 0; tableRow < model.getRowCount(); ++tableRow) {
			String playerName = model.getValueAt(tableRow,
					indexOf(PLAYER_COLUMN)).toString();
			int playerIndex = players.indexOf(playerName);
			if (playerIndex >= 0) {
				model.setValueAt(
						(Integer) model.getValueAt(tableRow,
								indexOf(TOTAL_SCORE_COLUMN))
								+ goals.get(playerIndex), tableRow,
						indexOf(TOTAL_SCORE_COLUMN));
				switch (results.get(playerIndex)) {
				case WIN:
					model.setValueAt((Integer) model.getValueAt(tableRow,
							indexOf(GAMES_WON_COLUMN)) + 1, tableRow,
							indexOf(GAMES_WON_COLUMN));
					break;
				case LOSE:
					model.setValueAt((Integer) model.getValueAt(tableRow,
							indexOf(GAMES_LOST_COLUMN)) + 1, tableRow,
							indexOf(GAMES_LOST_COLUMN));
					break;
				case TIE:
					model.setValueAt((Integer) model.getValueAt(tableRow,
							indexOf(GAMES_TIED_COLUMN)) + 1, tableRow,
							indexOf(GAMES_TIED_COLUMN));
					break;
				}
				playersToAdd.remove(playerName);
			}
		}
	}

	private void addNewPlayers(List<String> players, List<Integer> goals,
			List<GameResult> results, Set<String> playersToAdd,
			DefaultTableModel model) {
		for (String playerName : playersToAdd) {
			int playerIndex = players.indexOf(playerName);
			int numVictories = results.get(playerIndex) == GameResult.WIN ? 1
					: 0;
			int numDefeats = results.get(playerIndex) == GameResult.LOSE ? 1
					: 0;
			int numTies = results.get(playerIndex) == GameResult.TIE ? 1 : 0;
			Object row[] = new Object[headers.length];
			row[indexOf(PLAYER_COLUMN)] = playerName;
			row[indexOf(GAMES_WON_COLUMN)] = numVictories;
			row[indexOf(GAMES_LOST_COLUMN)] = numDefeats;
			row[indexOf(GAMES_TIED_COLUMN)] = numTies;
			row[indexOf(TOTAL_SCORE_COLUMN)] = goals.get(players
					.indexOf(playerName));
			model.addRow(row);
		}
	}

	public JTable getTableView() {
		return statsTable;
	}

	public List<PlayerStatistics> getPlayersStats() {
		List<PlayerStatistics> stats = new ArrayList<PlayerStatistics>(
				statsTable.getRowCount());
		for (int i = 0; i < statsTable.getRowCount(); ++i) {
			stats.add(getRow(i));
		}
		return stats;
	}

	public void save(String filename) {
		File csv = new File(filename);
		CSVGenerator logger = null;
		try {
			logger = new CSVGenerator(csv.getAbsolutePath(), headers);
			DefaultTableModel model = (DefaultTableModel) statsTable.getModel();
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
