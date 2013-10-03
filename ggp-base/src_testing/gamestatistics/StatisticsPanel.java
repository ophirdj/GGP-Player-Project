package gamestatistics;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.ggp.base.server.event.ServerMatchUpdatedEvent;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.observer.Event;
import org.ggp.base.util.observer.Observer;
import org.ggp.base.util.ui.JLabelBold;

@SuppressWarnings("serial")
public final class StatisticsPanel extends JPanel implements Observer
{
	private final JTable statsTable;
	private final TableRowSorter<TableModel> sorter;
	
	public StatisticsPanel()
	{
		super(new BorderLayout());
		
        DefaultTableModel model = new DefaultTableModel();		
        model.addColumn("Player");
        model.addColumn("Games Won");
        model.addColumn("Games Lost");
        model.addColumn("Games Tied");
        model.addColumn("Total Score");
        
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

		add(new JLabelBold("Statistics"), BorderLayout.NORTH);
		add(new JScrollPane(statsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
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
				int numVictories = (Integer)model.getValueAt(i, 1);
				int numDefeats = (Integer)model.getValueAt(i, 2);
				int numTies = (Integer)model.getValueAt(i, 3);
				int oldScore = (Integer)model.getValueAt(i, 4);
				model.setValueAt(oldScore + goals.get(playerIndex), i, 4);
				if(isVictory(goals.get(playerIndex), goals)) {
					model.setValueAt(numVictories + 1, i, 1);
				}
				if (isDefeat(goals.get(playerIndex), goals)) {
					model.setValueAt(numDefeats + 1, i, 2);
				}
				if(isTie(goals)) {
					model.setValueAt(numTies + 1, i, 3);
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
}
