package player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import debugging.Verbose;

public class PrintAction implements ActionListener {

	private ConfigurationPanel configPanel;

	public PrintAction(ConfigurationPanel configurationPanel) {
		this.configPanel = configurationPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Verbose.printVerbose("MinMax depth = " + configPanel.getMinMaxDepth(), Verbose.GRAPHIC_VAL);
		Verbose.printVerbose("example amount = " + configPanel.getExampleAmount(), Verbose.GRAPHIC_VAL);
		Verbose.printVerbose("player type = " + configPanel.getParaPlayerFactory(), Verbose.GRAPHIC_VAL);
		Verbose.printVerbose("simulator factory type = " + configPanel.getSimulatorFactory(), Verbose.GRAPHIC_VAL);
		Verbose.printVerbose("minmax factory type = " + configPanel.getMinmaxFactory(), Verbose.GRAPHIC_VAL);
		Verbose.printVerbose("builder factory type = " + configPanel.getBuilderFactory(), Verbose.GRAPHIC_VAL);
	}

}
