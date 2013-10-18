package playerdetails;

import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import logging.CSVGenerator;
import minmax.IMinMax.MinMaxEvent;

import org.ggp.base.player.gamer.event.GamerNewMatchEvent;
import org.ggp.base.util.ui.table.JZebraTable;

import utils.Verbose;

public final class MinMaxDetail {

	private static final String MOVE_COLUMN = "Move";
	private static final String TIME_COLUMN = "Time (secs)";
	private static final String BRANCHING_FACTOR_COLUMN = "Branching Factor";
	private static final String CACHE_HITS_COLUMN = "Cache Hits";
	private static final String CACHED_COLUMN = "Cached";
	private static final String TERMINAL_COLUMN = "Terminal";
	private static final String PRUNED_COLUMN = "Pruned";
	private static final String EXPANDED_COLUMN = "Expanded";
	private static final String EXPLORED_COLUMN = "Explored";
	private static final String DEPTH_COLUMN = "Depth";

	private static final String headers[] = { DEPTH_COLUMN, EXPLORED_COLUMN,
			EXPANDED_COLUMN, PRUNED_COLUMN, TERMINAL_COLUMN, CACHED_COLUMN,
			CACHE_HITS_COLUMN, BRANCHING_FACTOR_COLUMN, TIME_COLUMN,
			MOVE_COLUMN };

	public final JTable table;
	private final DefaultTableModel model;
	private String logTableName;

	public MinMaxDetail() {
		this.model = new DefaultTableModel();
		for (String header : headers) {
			model.addColumn(header);
		}

		table = new JZebraTable(model) {
			private static final long serialVersionUID = 6599141739431637522L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == model.findColumn(MOVE_COLUMN))
					return String.class;
				if (colIndex == model.findColumn(TIME_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(BRANCHING_FACTOR_COLUMN))
					return Double.class;
				if (colIndex == model.findColumn(CACHE_HITS_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(CACHED_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(TERMINAL_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(PRUNED_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(EXPANDED_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(EXPLORED_COLUMN))
					return Integer.class;
				if (colIndex == model.findColumn(DEPTH_COLUMN))
					return Integer.class;
				return Object.class;
			}
		};
	}

	public void onNewGame(GamerNewMatchEvent event) {
		model.setRowCount(0);
		createLogTableFilename(event);
	}

	private void createLogTableFilename(GamerNewMatchEvent event) {
		logTableName = event.getMatch().getMatchId() + "."
				+ event.getRoleName() + "." + System.currentTimeMillis()
				+ ".csv";
	}

	public void saveMinMaxData(String directory) {
		CSVGenerator logger = null;
		try {
			logger = new CSVGenerator(directory, logTableName, headers);
			for (int row = 0; row < model.getRowCount(); ++row) {
				Object rowData[] = new Object[model.getColumnCount()];
				for (int column = 0; column < rowData.length; ++column) {
					rowData[column] = model.getValueAt(row, column);
				}
				logger.addRow(rowData);
			}
		} catch (IOException e) {
			Verbose.printVerboseError("failed to save minmax table",
					Verbose.UNEXPECTED_VALUE);
		} finally {
			if (logger != null) {
				try {
					logger.close();
				} catch (IOException e) {
					Verbose.printVerboseError("could not close logger",
							Verbose.UNEXPECTED_VALUE);
					e.printStackTrace();
				}
				logTableName = null;
			}
		}
	}

	public void onMinMaxEvent(MinMaxEvent event) {
		String move = event.move.toString();
		Integer exploredNodes = event.exploredNodes;
		Integer expandedNodes = event.expandedNodes;
		Integer prunedNodes = event.prunedNodes;
		Integer terminalNodes = event.terminalNodes;
		Integer cacheSize = event.nodesInCache;
		Integer cacheHits = event.cacheHits;
		Integer depth = event.searchDepth;
		Double branchingFactor = event.averageBranchingFactor;
		Long time = millis2seconds(event.duration);

		updateTable(move, exploredNodes, expandedNodes, prunedNodes,
				terminalNodes, cacheSize, cacheHits, depth, branchingFactor,
				time);
	}

	private long millis2seconds(long millis) {
		return millis / 1000;
	}

	private void updateTable(String move, Integer exploredNodes,
			Integer expandedNodes, Integer prunedNodes, Integer terminalNodes,
			Integer cacheSize, Integer cacheHits, Integer depth,
			Double branchingFactor, Long time) {
		Object row[] = new Object[headers.length];

		row[model.findColumn(MOVE_COLUMN)] = move;
		row[model.findColumn(EXPLORED_COLUMN)] = exploredNodes;
		row[model.findColumn(EXPANDED_COLUMN)] = expandedNodes;
		row[model.findColumn(PRUNED_COLUMN)] = prunedNodes;
		row[model.findColumn(TERMINAL_COLUMN)] = terminalNodes;
		row[model.findColumn(CACHED_COLUMN)] = cacheSize;
		row[model.findColumn(CACHE_HITS_COLUMN)] = cacheHits;
		row[model.findColumn(DEPTH_COLUMN)] = depth;
		row[model.findColumn(TIME_COLUMN)] = time;
		row[model.findColumn(MOVE_COLUMN)] = move;

		model.addRow(row);
	}

}
