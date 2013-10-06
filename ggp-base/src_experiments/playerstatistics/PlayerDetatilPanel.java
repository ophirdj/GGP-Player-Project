package playerstatistics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import minmax.MinMax;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.player.gamer.event.GamerNewMatchEvent;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.ui.table.JZebraTable;

public class PlayerDetatilPanel extends DetailPanel {
	
	private static final long serialVersionUID = -7086442989371557835L;
	private final JZebraTable minmaxTable;

	public PlayerDetatilPanel() {
		super(new GridBagLayout());
		
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Explored Nodes");
		model.addColumn("Expanded Nodes");
		model.addColumn("Cached Nodes");
		model.addColumn("Maximal Depth");
		model.addColumn("Branching Factor");
		model.addColumn("Time (secs)");
		model.addColumn("Selected Move");

		minmaxTable = new JZebraTable(model) {

			private static final long serialVersionUID = 487437233903566016L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == 0) return Integer.class;
				if (colIndex == 1) return Integer.class;
				if (colIndex == 2) return Integer.class;
				if (colIndex == 3) return Integer.class;
				if (colIndex == 4) return Double.class;
				if (colIndex == 5) return Long.class;
				if (colIndex == 6) return String.class;
				return Object.class;
			}
			
		};
		minmaxTable.setShowHorizontalLines(true);
		minmaxTable.setShowVerticalLines(true);

		this.add(new JScrollPane(minmaxTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));
	}

	@Override
	public void observe(Event event) {
		if (event instanceof GamerNewMatchEvent) {
			observe((GamerNewMatchEvent) event);
		} else if (event instanceof MinMax.MinMaxEvent) {
			observe((MinMax.MinMaxEvent) event);
		}
	}
	
	private void observe(GamerNewMatchEvent event) {
		DefaultTableModel model = (DefaultTableModel) minmaxTable.getModel();
		model.setRowCount(0);
	}

	private void observe(MinMax.MinMaxEvent event) {
		String move = event.getMove().toString();
		Integer exploredNodes = event.getExploredNodes();
		Integer expandedNodes = event.getExpandedNodes();
		Integer cacheSize = event.getNodesInCache();
		Integer depth = event.getSearchDepth();
		Double branchingFactor = event.getAverageBranchingFactor();
		Long time = event.getDuration() / 1000;

		DefaultTableModel model = (DefaultTableModel) minmaxTable.getModel();
		model.addRow(new Object[] { exploredNodes, expandedNodes, cacheSize, depth, branchingFactor, time, move });
	}

}
