package player;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import labeler.IStateLabelerFactory;
import minmax.IMinMaxFactory;

import org.ggp.base.apps.player.config.ConfigPanel;
import org.ggp.base.util.reflection.ProjectSearcher;

import simulator.ISimulatorFactory;
import utils.Verbose;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import wekaclassifier.IWekaClassifier;
import classifier.IClassifierFactory;
import classifier.comparer.simple.SimpleComparerClassifierFactory;
import classifier.heuristic.simple.SimpleHeuristicClassifierFactory;

public final class ConfigurationPanel extends ConfigPanel {

	private static final long serialVersionUID = -581259385199768198L;

	private final JComboBox<IStateLabelerFactory> labelerList = initComboBox(IStateLabelerFactory.class);
	private final JComboBox<ISimulatorFactory> simulatorList = initComboBox(ISimulatorFactory.class);
	private final JComboBox<IMinMaxFactory> minMaxList = initComboBox(IMinMaxFactory.class);
	private final JComboBox<IWekaClassifier> wekaClassifierList = initComboBox(IWekaClassifier.class);

	public final JCheckBox savePlayerData = new JCheckBox("Save Data?", false);
	private final JCheckBox simulatorAnytime = new JCheckBox("Anytime?", false);
	private final JCheckBox minmaxCached = new JCheckBox("Cached?", false);
	private final JCheckBox minmaxAnytime = new JCheckBox("Anytime?", false);

	private final JSpinner numSimulations = new JSpinner(
			new SpinnerNumberModel(100, 1, 9999, 50));
	private final JSpinner minmaxDepth = new JSpinner(new SpinnerNumberModel(4,
			1, 20, 1));

	private final JRadioButton useHeuristic = new JRadioButton("Absolute");
	private final JRadioButton useCompare = new JRadioButton("Relative");

	private final ButtonGroup classificationGroup = new ButtonGroup();

	public ConfigurationPanel() {
		super(new GridBagLayout());

		simulatorAnytime.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				numSimulations.setEnabled(!simulatorAnytime.isSelected());
			}

		});

		minmaxAnytime.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				minmaxDepth.setEnabled(!minmaxAnytime.isSelected());
			}

		});

		classificationGroup.add(useHeuristic);
		classificationGroup.add(useCompare);

		wekaClassifierList.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				IWekaClassifier classifier = getWekaClassifer();
				Capabilities capabilities = classifier.getCapabilities();
				useCompare.setEnabled(capabilities
						.handles(Capability.BINARY_CLASS));
				useHeuristic.setEnabled(capabilities
						.handles(Capability.NUMERIC_CLASS));
				ButtonModel selection = classificationGroup.getSelection();
				if (selection != null && selection.isEnabled()) {
					return;
				}
				Enumeration<AbstractButton> buttonEnumeration = classificationGroup
						.getElements();
				while (buttonEnumeration.hasMoreElements()) {
					AbstractButton button = buttonEnumeration.nextElement();
					if (button.isEnabled()) {
						button.setSelected(true);
						break;
					}
				}
			}

		});

		{ // need to do this to invoke item listener to prevent bug
			IWekaClassifier c = wekaClassifierList.getItemAt(0);
			wekaClassifierList.removeItemAt(0);
			wekaClassifierList.addItem(c);
		}

		int rowCount = 0;

		add(new JLabel("Labeler:"), new GridBagConstraints(0, rowCount, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(labelerList, new GridBagConstraints(1, rowCount++, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(new JLabel("Simulator:"), new GridBagConstraints(0, rowCount, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(simulatorList, new GridBagConstraints(1, rowCount, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(simulatorAnytime, new GridBagConstraints(1, rowCount, 1, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));
		add(new JLabel("Simulations:"), new GridBagConstraints(2, rowCount, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		add(numSimulations, new GridBagConstraints(3, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(new JLabel("Algorithm:"), new GridBagConstraints(0, rowCount, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(minMaxList, new GridBagConstraints(1, rowCount, 3, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(minmaxAnytime, new GridBagConstraints(1, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));
		add(new JLabel("Depth:"), new GridBagConstraints(2, rowCount, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(minmaxDepth, new GridBagConstraints(3, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(minmaxCached, new GridBagConstraints(1, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(new JLabel("Classifier:"), new GridBagConstraints(0, rowCount, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(wekaClassifierList, new GridBagConstraints(1, rowCount, 3, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));

		++rowCount;

		add(new JLabel("Type:"), new GridBagConstraints(1, rowCount, 1, 1, 0.0,
				0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 1, 5), 5, 5));
		add(useHeuristic, new GridBagConstraints(2, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 1, 5), 5, 5));
		add(useCompare, new GridBagConstraints(3, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5,
						5, 1, 5), 5, 5));

		++rowCount;

		add(savePlayerData, new GridBagConstraints(0, rowCount, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 1, 5), 5, 5));
	}

	@SuppressWarnings("unchecked")
	private <T> JComboBox<T> initComboBox(Class<T> type) {
		JComboBox<T> combobox = new JComboBox<T>();
		List<Class<?>> items = ProjectSearcher.getAllClassesThatAre(type);
		ListIterator<Class<?>> iterator = items.listIterator();
		while (iterator.hasNext()) {
			try {
				T item = (T) iterator.next().newInstance();
				combobox.addItem(item);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				iterator.remove();
			}
		}
		return combobox;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		for (Component component : getComponents()) {
			component.setEnabled(isEnabled);
		}
	}

	public int getMinMaxDepth() {
		return (int) minmaxDepth.getValue();
	}

	public int getExampleAmount() {
		return (int) numSimulations.getValue();
	}

	public boolean isSimulatorAnytime() {
		return simulatorAnytime.isSelected();
	}

	public boolean isMinMaxAnytime() {
		return minmaxAnytime.isSelected();
	}

	public boolean isMinMaxCached() {
		return minmaxCached.isSelected();
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

	public IClassifierFactory getClassifierFactory() {
		if (useCompare.isSelected()) {
			return new SimpleComparerClassifierFactory();
		} else if (useHeuristic.isSelected()) {
			return new SimpleHeuristicClassifierFactory();
		} else {
			Verbose.printVerboseError("No compatible builder selected",
					Verbose.UNEXPECTED_VALUE);
			return null;
		}
	}

	public IWekaClassifier getWekaClassifer() {
		return (IWekaClassifier) wekaClassifierList.getSelectedItem();
	}

}
