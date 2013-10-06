package playerstatistics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import logging.CSVGenerator;
import minmax.MinMax;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.player.gamer.event.GamerCompletedMatchEvent;
import org.ggp.base.player.gamer.event.GamerNewMatchEvent;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.ui.table.JZebraTable;

public class PlayerDetatilPanel extends DetailPanel {

	private static final long serialVersionUID = -7086442989371557835L;
	private static final String headers[] = { "Explored", "Expanded", "Pruned",
			"Terminal", "Cached", "Cache Hits", "Depth", "Branching Factor",
			"Time (secs)", "Move" };
	private final JZebraTable minmaxTable;
	private final String playerName;
	private String logPath;
	private JCheckBox savePlayerData;

	public PlayerDetatilPanel(String playerName, JCheckBox savePlayerData) {
		super(new GridBagLayout());
		this.playerName = playerName;
		this.savePlayerData = savePlayerData;

		DefaultTableModel model = new DefaultTableModel();
		for (String header : headers) {
			model.addColumn(header);
		}

		minmaxTable = new JZebraTable(model) {

			private static final long serialVersionUID = 487437233903566016L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == 0)
					return Integer.class;
				if (colIndex == 1)
					return Integer.class;
				if (colIndex == 2)
					return Integer.class;
				if (colIndex == 3)
					return Integer.class;
				if (colIndex == 4)
					return Integer.class;
				if (colIndex == 5)
					return Integer.class;
				if (colIndex == 6)
					return Integer.class;
				if (colIndex == 7)
					return Double.class;
				if (colIndex == 8)
					return Long.class;
				if (colIndex == 9)
					return String.class;
				return Object.class;
			}

		};
		minmaxTable.setShowHorizontalLines(true);
		minmaxTable.setShowVerticalLines(true);

		this.add(new JScrollPane(minmaxTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 5, 5));
	}

	@Override
	public void observe(Event event) {
		if (event instanceof GamerNewMatchEvent) {
			observe((GamerNewMatchEvent) event);
		} else if (event instanceof MinMax.MinMaxEvent) {
			observe((MinMax.MinMaxEvent) event);
		} else if (event instanceof GamerCompletedMatchEvent) {
			if(savePlayerData.isSelected()) {
				saveMatchTable();
			}
		}
	}

	private void saveMatchTable() {
		try {
			CSVGenerator logger = new CSVGenerator(logPath, headers);
			DefaultTableModel model = (DefaultTableModel) minmaxTable
					.getModel();
			for (int row = 0; row < model.getRowCount(); ++row) {
				Object rowData[] = new Object[model.getColumnCount()];
				for (int column = 0; column < rowData.length; ++column) {
					rowData[column] = model.getValueAt(row, column);
				}
				logger.addRow(rowData);
			}
			logger.close();
			logPath = null;
		} catch (IOException e) {

		}
	}

	private void observe(GamerNewMatchEvent event) {
		DefaultTableModel model = (DefaultTableModel) minmaxTable.getModel();
		model.setRowCount(0);
		
		File playersDir = new File(System.getProperty("user.home"),
				"ggp-player-details");
		if (!playersDir.exists()) {
			playersDir.mkdir();
		}
		File playerDir = new File(playersDir, playerName);
		if (!playerDir.exists()) {
			playerDir.mkdir();
		}
		String logID = event.getMatch().getMatchId() + "."
				+ event.getRoleName() + "." + System.currentTimeMillis();
		File log = new File(playerDir, logID + ".csv");
		logPath = log.getAbsolutePath();
	}

	private void observe(MinMax.MinMaxEvent event) {
		String move = event.getMove().toString();
		Integer exploredNodes = event.getExploredNodes();
		Integer expandedNodes = event.getExpandedNodes();
		Integer prunedNodes = event.getPrunedNodes();
		Integer terminalNodes = event.getTerminalNodes();
		Integer cacheSize = event.getNodesInCache();
		Integer cacheHits = event.getCacheHits();
		Integer depth = event.getSearchDepth();
		Double branchingFactor = event.getAverageBranchingFactor();
		Long time = event.getDuration() / 1000;

		DefaultTableModel model = (DefaultTableModel) minmaxTable.getModel();
		model.addRow(new Object[] { exploredNodes, expandedNodes, prunedNodes,
				terminalNodes, cacheSize, cacheHits, depth, branchingFactor,
				time, move });
	}

}
