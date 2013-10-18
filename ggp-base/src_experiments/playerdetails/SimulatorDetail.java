package playerdetails;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import logging.CSVGenerator;

import org.ggp.base.player.gamer.event.GamerNewMatchEvent;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.ui.CloseableTabs;
import org.ggp.base.util.ui.table.JZebraTable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;

import simulator.ISimulator.SimulatorEvent;
import states.LabeledState;
import utils.Verbose;

public final class SimulatorDetail extends JPanel {

	private static final long serialVersionUID = -1981439263636553205L;

	private static final String CONTENTS_COLUMN = "Contents";

	private static final String headers[] = { CONTENTS_COLUMN };

	private final JTabbedPane tabsPane;
	private final JTable table;
	private final DefaultTableModel model;
	private final TableRowSorter<TableModel> sorter;
	private Map<GdlSentence, HistogramDataset> histograms;
	private String logTableName;

	public SimulatorDetail() {
		super(new BorderLayout());

		histograms = new HashMap<GdlSentence, HistogramDataset>();

		model = new DefaultTableModel();
		for (String header : headers) {
			model.addColumn(header);
		}

		table = new JZebraTable(model) {

			private static final long serialVersionUID = 3779097598780114455L;

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int colIndex) {
				if (colIndex == model.findColumn(CONTENTS_COLUMN))
					return GdlSentence.class;
				return Object.class;
			}

		};

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.getSelectedRow();
				if ((e.getClickCount() == 2) && (row >= 0)
						&& (row < table.getRowCount())) {
					showChart((GdlSentence) model.getValueAt(row,
							model.findColumn(CONTENTS_COLUMN)));
				}
			}
		});

		sorter = new TableRowSorter<TableModel>(model);
		sorter.setComparator(model.findColumn(CONTENTS_COLUMN),
				new Comparator<GdlSentence>() {

					@Override
					public int compare(GdlSentence s1, GdlSentence s2) {
						return s1.toString().compareTo(s2.toString());
					}

				});
		sorter.setSortKeys(Arrays.asList(new SortKey[] { new SortKey(model
				.findColumn(CONTENTS_COLUMN), SortOrder.DESCENDING) }));
		table.setRowSorter(sorter);

		tabsPane = new JTabbedPane();
		tabsPane.addTab("Table", new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		add(tabsPane, BorderLayout.CENTER);
	}

	public void onNewGame(GamerNewMatchEvent event) {
		model.setRowCount(0);
		histograms.clear();
		createLogTablePath(event);
	}

	private void createLogTablePath(GamerNewMatchEvent event) {
		logTableName = event.getMatch().getMatchId() + "."
				+ event.getRoleName() + "." + System.currentTimeMillis()
				+ ".csv";
	}

	public void saveSimulatorData(String directory) {
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

	public void onSimulatorEvent(SimulatorEvent event) {
		addContents(filterContents(event.contents, getTableContents()));
		for (GdlSentence content : getTableContents()) {
			histograms.put(content,
					createHistogram(event.labeledStates, content));
		}
	}

	private HistogramDataset createHistogram(
			Collection<LabeledState> labeledStates, GdlSentence content) {
		HistogramDataset dataset = new HistogramDataset();
		List<Double> trueValues = new ArrayList<Double>();
		List<Double> falseValues = new ArrayList<Double>();
		for (LabeledState state : labeledStates) {
			if (state.getState().getContents().contains(content)) {
				trueValues.add(state.getValue());
			} else {
				falseValues.add(state.getValue());
			}
		}
		if (trueValues.size() > 0) {
			double[] arrayTrueValues = new double[trueValues.size()];
			for (int i = 0; i < trueValues.size(); ++i) {
				arrayTrueValues[i] = trueValues.get(i);
			}
			dataset.addSeries("TRUE", arrayTrueValues, 10);
		}
		if (falseValues.size() > 0) {
			double[] arrayFalseValues = new double[falseValues.size()];
			for (int i = 0; i < falseValues.size(); ++i) {
				arrayFalseValues[i] = falseValues.get(i);
			}
			dataset.addSeries("FALSE", arrayFalseValues, 10);
		}
		return dataset;
	}

	private void addContents(Set<GdlSentence> contents) {
		for (GdlSentence content : contents) {
			Object row[] = new Object[headers.length];
			row[model.findColumn(CONTENTS_COLUMN)] = content;
			model.addRow(row);
		}
	}

	private Set<GdlSentence> filterContents(Set<GdlSentence> eventContents,
			Set<GdlSentence> tableContents) {
		Set<GdlSentence> contents = new HashSet<GdlSentence>(eventContents);
		contents.removeAll(tableContents);
		return contents;
	}

	private Set<GdlSentence> getTableContents() {
		Set<GdlSentence> tableContents = new HashSet<GdlSentence>(
				model.getRowCount());
		for (int row = 0; row < model.getRowCount(); ++row) {
			tableContents.add((GdlSentence) model.getValueAt(row,
					model.findColumn(CONTENTS_COLUMN)));
		}
		return tableContents;
	}

	private void showChart(GdlSentence content) {
		HistogramDataset dataset = histograms.get(content);
		if (dataset == null) {
			return;
		}
		for (int i = 0; i < tabsPane.getTabCount(); ++i) {
			String tabName = tabsPane.getTitleAt(i);
			if (tabName != null && tabName.equals(content.toString())) {
				tabsPane.setSelectedIndex(i);
				return;
			}
		}
		JFreeChart chart = ChartFactory.createHistogram(content.toString(),
				"score", "amount", dataset, PlotOrientation.VERTICAL, true,
				true, false);
		ChartPanel chartPanel = new ChartPanel(chart);
		CloseableTabs.addClosableTab(tabsPane, chartPanel, content.toString(),
				addTabCloseButton(chartPanel));
		tabsPane.setSelectedIndex(tabsPane.getTabCount() - 1);
	}

	private AbstractAction addTabCloseButton(final ChartPanel chartPanel) {
		return new AbstractAction("x") {

			private static final long serialVersionUID = 846256002308622567L;

			public void actionPerformed(ActionEvent evt) {
				tabsPane.remove(chartPanel);
			}
		};
	}

}
