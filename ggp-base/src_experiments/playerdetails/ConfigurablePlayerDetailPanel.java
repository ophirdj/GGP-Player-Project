package playerdetails;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import minmax.IMinMax.MinMaxEvent;
import simulator.ISimulator.SimulatorEvent;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.player.gamer.event.GamerCompletedMatchEvent;
import org.ggp.base.player.gamer.event.GamerNewMatchEvent;
import org.ggp.base.util.observer.Event;

public final class ConfigurablePlayerDetailPanel extends DetailPanel {

	private static final long serialVersionUID = 8147578089076923090L;

	private final String playerName;
	private final JCheckBox savePlayerData;
	private final SimulatorDetail simulatorDetail;
	private final MinMaxDetail minmaxDetail;

	public ConfigurablePlayerDetailPanel(String playerName,
			JCheckBox savePlayerData) {
		super(new GridBagLayout());
		this.playerName = playerName;
		this.savePlayerData = savePlayerData;
		this.simulatorDetail = new SimulatorDetail();
		this.minmaxDetail = new MinMaxDetail();

		add(simulatorDetail,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.7,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 5, 5));

		add(new JScrollPane(minmaxDetail.table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				new GridBagConstraints(0, 1, 1, 1, 1.0, 0.3,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 5, 5));
	}

	@Override
	public void observe(Event event) {
		if (event instanceof GamerNewMatchEvent) {
			simulatorDetail.onNewGame((GamerNewMatchEvent) event);
			minmaxDetail.onNewGame((GamerNewMatchEvent) event);
		} else if (event instanceof MinMaxEvent) {
			minmaxDetail.onMinMaxEvent((MinMaxEvent) event);
		} else if (event instanceof SimulatorEvent) {
			simulatorDetail.onSimulatorEvent((SimulatorEvent) event);
		} else if (event instanceof GamerCompletedMatchEvent) {
			if (savePlayerData.isSelected()) {
				String playerDir = getPlayerDir();
				simulatorDetail.saveSimulatorData(playerDir);
				minmaxDetail.saveMinMaxData(playerDir);
			}
		}
	}

	private String getPlayerDir() {
		File playersDataDirrectory = new File(System.getProperty("user.home"),
				"ggp-player-details");
		File playerDirectory = new File(playersDataDirrectory, playerName);
		return playerDirectory.getAbsolutePath();
	}

}
