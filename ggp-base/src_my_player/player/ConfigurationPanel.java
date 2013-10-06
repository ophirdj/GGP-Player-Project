package player;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import minmax.MinMaxFactory;
import minmax.MinMaxType;

import org.ggp.base.apps.player.config.ConfigPanel;

import simulator.MapValueSimulatorFactory;
import simulator.SimulatorType;
import debugging.Verbose;

public class ConfigurationPanel extends ConfigPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -581259385199768198L;
	
	
	
	private final JSpinner exampleAmountSpiner;
	private final JSpinner minMaxDepthSpinner;


	private JPanel spinnersPanel;
	private GridBagConstraints spinnersPanelConstraints;

	private JPanel checkPanel;
	private GridBagConstraints checkPanelConstraints;

	private JButton printButton;


	private JComboBox<PlayerType> playerTypeList;
	private JComboBox<MinMaxType> minMaxTypeList;
	private JComboBox<SimulatorType> simulatorList;
	private JComboBox<BuilderType> builderList;
	
	public ConfigurationPanel() {
		super(new GridBagLayout());
		this.spinnersPanel = new JPanel(new GridBagLayout());
		exampleAmountSpiner = new JSpinner(new SpinnerNumberModel(100, 1, 99999, 50));
		minMaxDepthSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
		
		int panelNumber = 0;
		
		int spinnerRowCount = 0;
		spinnersPanel.add(new JLabel("Examples amount:"), new GridBagConstraints(0, spinnerRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(exampleAmountSpiner, new GridBagConstraints(1, spinnerRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(new JLabel("MinMax Depth:"), new GridBagConstraints(0, spinnerRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanel.add(minMaxDepthSpinner, new GridBagConstraints(1, spinnerRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		spinnersPanelConstraints = new GridBagConstraints(panelNumber++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5);
		this.add(spinnersPanel, spinnersPanelConstraints);
		
		int checkRowCount = 0;
		this.checkPanel = new JPanel(new GridBagLayout());
		

		this.playerTypeList = new JComboBox<PlayerType>(PlayerType.values());
		this.minMaxTypeList = new JComboBox<MinMaxType>(MinMaxType.values());
		this.simulatorList = new JComboBox<SimulatorType>(SimulatorType.values());
		this.builderList = new JComboBox<BuilderType>(BuilderType.values());
		
		checkPanel.add(new JLabel("player type:"), new GridBagConstraints(0, checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(playerTypeList, new GridBagConstraints(1, checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("min max kind:"), new GridBagConstraints(0, checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(minMaxTypeList, new GridBagConstraints(1, checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("simulator kind:"), new GridBagConstraints(0, checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(simulatorList, new GridBagConstraints(1, checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(new JLabel("builder kind:"), new GridBagConstraints(0, checkRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		checkPanel.add(builderList, new GridBagConstraints(1, checkRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		checkPanelConstraints = new GridBagConstraints(panelNumber++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 5, 5);
		this.add(checkPanel, checkPanelConstraints);
		
		if(Verbose.isVerbose(Verbose.GRAPHIC_VAL)){
			this.printButton = new JButton("print info");
			printButton.addActionListener(new PrintAction(this));
			this.add(printButton, new GridBagConstraints(panelNumber++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		}
	}
	
	

	
	public int getMinMaxDepth(){
		return (int)minMaxDepthSpinner.getValue();
	}
	
	public int getExampleAmount(){
		return (int)exampleAmountSpiner.getValue();
	}
	
	public ParaStateMachinePlayerFactory getParaPlayerFactory(){
		return ((PlayerType)playerTypeList.getSelectedItem()).getParaPlayerFactory();
	}




	public MapValueSimulatorFactory getSimulatorFactory() {
		return ((SimulatorType)simulatorList.getSelectedItem()).getSimulatorFactory();
	}




	public MinMaxFactory getMinmaxFactory() {
		return ((MinMaxType)minMaxTypeList.getSelectedItem()).getFactory();
	}




	public BuilderFactory getBuilderFactory() {
		return ((BuilderType)builderList.getSelectedItem()).getBuilderFactory();
	}

}
