package player;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;

import labeler.IStateLabelerFactory;
import minmax.IMinMaxFactory;

import org.ggp.base.apps.player.config.ConfigPanel;
import org.ggp.base.util.reflection.ProjectSearcher;

import simulator.ISimulatorFactory;
import classifier.IClassifierFactory;

public class ConfigurationPanel extends ConfigPanel {

	private static final long serialVersionUID = -2406490153748470242L;

	private final JSpinner exampleAmountSpinner;
	private final JSpinner minMaxDepthSpinner;

	private JPanel spinnersPanel;
	private GridBagConstraints spinnersPanelConstraints;

	private JPanel checkPanel;
	private GridBagConstraints checkPanelConstraints;

	// private JButton printButton;

	private JComboBox<IMinMaxFactory> minMaxList;
	private JComboBox<IStateLabelerFactory> labelerList;
	private JComboBox<ISimulatorFactory> simulatorList;
	private JComboBox<IClassifierFactory> classifierList;
	public final JCheckBox savePlayerData;

	public ConfigurationPanel() {
		super(new GridBagLayout());
		this.spinnersPanel = new JPanel(new GridBagLayout());
		exampleAmountSpinner = new JSpinner(new SpinnerNumberModel(100, 1,
				99999, 50));
		minMaxDepthSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));

		int panelNumber = 0;

		int spinnerRowCount = 0;
		spinnersPanel.add(new JLabel("Number of simulations:"),
				new GridBagConstraints(0, spinnerRowCount, 1, 1, 0.0, 0.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(exampleAmountSpinner, new GridBagConstraints(1,
				spinnerRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(new JLabel("Min-Max depth:"), new GridBagConstraints(
				0, spinnerRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(minMaxDepthSpinner, new GridBagConstraints(1,
				spinnerRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanelConstraints = new GridBagConstraints(panelNumber++, 0, 1,
				1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5);
		this.add(spinnersPanel, spinnersPanelConstraints);

		int checkRowCount = 0;
		this.checkPanel = new JPanel(new GridBagLayout());

		List<Class<?>> labelersFactories = ProjectSearcher
				.getAllClassesThatAre(IStateLabelerFactory.class);
		this.labelerList = new JComboBox<IStateLabelerFactory>();
		ListIterator<Class<?>> iterator = labelersFactories.listIterator();
		while (iterator.hasNext()) {
			try {
				IStateLabelerFactory factory = (IStateLabelerFactory) iterator
						.next().newInstance();
				this.labelerList.addItem(factory);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				iterator.remove();
			}
		}

		List<Class<?>> simulatorsFactories = ProjectSearcher
				.getAllClassesThatAre(ISimulatorFactory.class);
		this.simulatorList = new JComboBox<ISimulatorFactory>();
		iterator = simulatorsFactories.listIterator();
		while (iterator.hasNext()) {
			try {
				ISimulatorFactory factory = (ISimulatorFactory) iterator.next()
						.newInstance();
				this.simulatorList.addItem(factory);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				iterator.remove();
			}
		}

		List<Class<?>> minmaxFactories = ProjectSearcher
				.getAllClassesThatAre(IMinMaxFactory.class);
		this.minMaxList = new JComboBox<IMinMaxFactory>();
		iterator = minmaxFactories.listIterator();
		while (iterator.hasNext()) {
			try {
				IMinMaxFactory factory = (IMinMaxFactory) iterator.next()
						.newInstance();
				this.minMaxList.addItem(factory);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				iterator.remove();
			}
		}

		List<Class<?>> classifierFactories = ProjectSearcher
				.getAllClassesThatAre(IClassifierFactory.class);
		this.classifierList = new JComboBox<IClassifierFactory>();
		iterator = classifierFactories.listIterator();
		while (iterator.hasNext()) {
			try {
				IClassifierFactory factory = (IClassifierFactory) iterator
						.next().newInstance();
				this.classifierList.addItem(factory);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				iterator.remove();
			}
		}

		checkPanel.add(new JLabel("Labeler type:"), new GridBagConstraints(0,
				checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(labelerList, new GridBagConstraints(1, checkRowCount++,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("Simulator type:"), new GridBagConstraints(0,
				checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(simulatorList, new GridBagConstraints(1,
				checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("Builder type:"), new GridBagConstraints(0,
				checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(classifierList, new GridBagConstraints(1,
				checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("Min-Max type:"), new GridBagConstraints(0,
				checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(minMaxList, new GridBagConstraints(1, checkRowCount++,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanelConstraints = new GridBagConstraints(panelNumber++, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.FIRST_LINE_END,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5);
		this.add(checkPanel, checkPanelConstraints);

		savePlayerData = new JCheckBox("Save data?", false);
		add(savePlayerData);
	}

	public void setEditble(boolean isEditable) {
		((DefaultEditor) exampleAmountSpinner.getEditor()).getTextField()
				.setEditable(isEditable);
		((DefaultEditor) minMaxDepthSpinner.getEditor()).getTextField()
				.setEditable(isEditable);
		simulatorList.setEditable(isEditable);
		labelerList.setEditable(isEditable);
		minMaxList.setEditable(isEditable);
		classifierList.setEditable(isEditable);
		savePlayerData.setEnabled(isEditable);
	}

	public int getMinMaxDepth() {
		return (int) minMaxDepthSpinner.getValue();
	}

	public int getExampleAmount() {
		return (int) exampleAmountSpinner.getValue();
	}

	public ISimulatorFactory getSimulatorFactory() {
		return (ISimulatorFactory) simulatorList.getSelectedItem();
	}

	public IStateLabelerFactory getLabelerFactory() {
		return (IStateLabelerFactory) labelerList.getSelectedItem();
	}

	public IMinMaxFactory getMinmaxFactory() {
		return (IMinMaxFactory) minMaxList.getSelectedItem();
	}

	public IClassifierFactory getStateClassifierFactory() {
		return (IClassifierFactory) classifierList.getSelectedItem();
	}
}
