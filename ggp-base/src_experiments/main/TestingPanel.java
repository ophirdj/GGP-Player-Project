package main;

import gamestatistics.StatisticsPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.ggp.base.apps.server.error.ErrorPanel;
import org.ggp.base.apps.server.history.HistoryPanel;
import org.ggp.base.apps.server.scheduling.SchedulingPanel;
import org.ggp.base.apps.server.states.StatesPanel;
import org.ggp.base.apps.server.visualization.VisualizationPanel;
import org.ggp.base.server.GameServer;
import org.ggp.base.util.crypto.BaseCryptography.EncodedKeyPair;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.game.GameRepository;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.match.Match;
import org.ggp.base.util.presence.PlayerPresence;
import org.ggp.base.util.presence.PlayerPresenceManager.InvalidHostportException;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;
import org.ggp.base.util.ui.CloseableTabs;
import org.ggp.base.util.ui.GameSelector;
import org.ggp.base.util.ui.JLabelBold;
import org.ggp.base.util.ui.NativeUI;
import org.ggp.base.util.ui.PlayerSelector;

public final class TestingPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -3528004351716680625L;

	private static void createAndShowGUI(TestingPanel serverPanel, String title) {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setPreferredSize(new Dimension(1300, 700));
		frame.getContentPane().add(serverPanel);

		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		NativeUI.setNativeUI();
		GdlPool.caseSensitive = false;

		final TestingPanel testingPanel = new TestingPanel();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				createAndShowGUI(testingPanel, "Testing Server");
			}
		});
	}

	private Game theGame;
	private EncodedKeyPair signingKeys;

	private final JPanel managerPanel;
	private final JTabbedPane matchesTabbedPane;
	private final JPanel gamePanel;
	private final JPanel playersPanel;

	private final SchedulingPanel schedulingPanel;
	private final StatisticsPanel statisticsPanel;

	private final List<JComboBox<String>> playerFields;
	private final List<JLabel> roleLabels;
	private final JButton runButton;

	private final JSpinner startClockSpinner;
	private final JSpinner playClockSpinner;
	private final JSpinner numberOfGamesSpinner;

	private final static String NOT_RUNNING = "no active games";
	private final JTextField gamesRemainedTextField;

	private final JCheckBox shouldPublish;
	private final JCheckBox shouldSave;
	private final JCheckBox switchRoles;

	private final GameSelector gameSelector;
	private final PlayerSelector playerSelector;
	private final JList<String> playerSelectorList;

	public TestingPanel() {
		super(new GridBagLayout());

		runButton = new JButton(runButtonMethod());
<<<<<<< HEAD
		startClockSpinner = new JSpinner(new SpinnerNumberModel(600, 5, 9999, 1));
		playClockSpinner = new JSpinner(new SpinnerNumberModel(300, 5, 9999, 1));
=======
		startClockSpinner = new JSpinner(new SpinnerNumberModel(600, 5, 600, 1));
		playClockSpinner = new JSpinner(new SpinnerNumberModel(15, 5, 300, 1));
>>>>>>> branch 'new_design' of https://github.com/ophirdj/GGP-Player-Project.git
		numberOfGamesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999,
				1));
		matchesTabbedPane = new JTabbedPane();

		gamesRemainedTextField = new JTextField(NOT_RUNNING);
		gamesRemainedTextField.setEditable(false);

		managerPanel = new JPanel(new GridBagLayout());
		gamePanel = new JPanel(new GridBagLayout());
		playersPanel = new JPanel(new GridBagLayout());

		roleLabels = new ArrayList<JLabel>();
		playerFields = new ArrayList<JComboBox<String>>();
		theGame = null;

		switchRoles = new JCheckBox("Switch roles between games?", true);
		shouldSave = new JCheckBox("Save match to disk?", true);
		shouldPublish = new JCheckBox("Publish match to the web?", false);

		runButton.setEnabled(false);

		gameSelector = new GameSelector();
		playerSelector = new PlayerSelector();
		playerSelectorList = playerSelector.getPlayerSelectorList();

		int nRowCount = 0;
		gamePanel.add(new JLabelBold("Match Setup"), new GridBagConstraints(0,
				nRowCount++, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 25, 5, 25), 0, 0));
		gamePanel.add(new JLabel("Repository:"), new GridBagConstraints(0,
				nRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(gameSelector.getRepositoryList(), new GridBagConstraints(
				1, nRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(new JLabel("Game:"), new GridBagConstraints(0, nRowCount,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(gameSelector.getGameList(), new GridBagConstraints(1,
				nRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(new JLabel("Start Clock:"), new GridBagConstraints(0,
				nRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(startClockSpinner, new GridBagConstraints(1, nRowCount++,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(new JLabel("Play Clock:"), new GridBagConstraints(0,
				nRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(playClockSpinner, new GridBagConstraints(1, nRowCount++,
				1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(new JLabel("Games:"), new GridBagConstraints(0,
				nRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(numberOfGamesSpinner, new GridBagConstraints(1,
				nRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(new JLabel("Remaining:"), new GridBagConstraints(0,
				nRowCount, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(gamesRemainedTextField, new GridBagConstraints(1,
				nRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(switchRoles, new GridBagConstraints(1, nRowCount++, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(shouldSave, new GridBagConstraints(1, nRowCount++, 1, 1,
				0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 1, 5), 5, 5));
		gamePanel.add(shouldPublish, new GridBagConstraints(1, nRowCount++, 1,
				1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 5, 5, 5), 5, 5));
		gamePanel.add(runButton, new GridBagConstraints(1, nRowCount, 1, 1,
				0.0, 1.0, GridBagConstraints.SOUTH,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

		nRowCount = 0;
		playersPanel.add(new JLabelBold("Player List"), new GridBagConstraints(
				0, nRowCount++, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 25, 5, 25), 0, 0));
		playersPanel.add(new JScrollPane(playerSelectorList),
				new GridBagConstraints(0, nRowCount++, 3, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(5, 25, 5, 25), 0, 0));
		playersPanel.add(new JButton(addPlayerButtonMethod()),
				new GridBagConstraints(0, nRowCount, 1, 1, 1.0, 1.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		playersPanel.add(new JButton(removePlayerButtonMethod()),
				new GridBagConstraints(1, nRowCount, 1, 1, 0.0, 1.0,
						GridBagConstraints.EAST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		playersPanel.add(new JButton(testPlayerButtonMethod()),
				new GridBagConstraints(2, nRowCount++, 1, 1, 1.0, 1.0,
						GridBagConstraints.WEST, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));

		nRowCount = 0;
		managerPanel.add(gamePanel, new GridBagConstraints(0, nRowCount++, 1,
				1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		managerPanel.add(new JSeparator(), new GridBagConstraints(0,
				nRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5));
		managerPanel.add(playersPanel, new GridBagConstraints(0, nRowCount++,
				1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 5));

		this.add(managerPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 5, 5));
		this.add(matchesTabbedPane, new GridBagConstraints(1, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 5, 5));

		gameSelector.getGameList().addActionListener(this);
		gameSelector.repopulateGameList();

		schedulingPanel = new SchedulingPanel();
		statisticsPanel = new StatisticsPanel();
		matchesTabbedPane.addTab("Overview", new OverviewPanel());
	}

	public void setSigningKeys(EncodedKeyPair keys) {
		signingKeys = keys;
	}

	class OverviewPanel extends JPanel {

		private static final long serialVersionUID = -9044010610644724689L;

		public OverviewPanel() {
			super(new GridBagLayout());
			add(schedulingPanel, new GridBagConstraints(0, 0, 1, 1, 2.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 5, 5));
			add(statisticsPanel, new GridBagConstraints(1, 0, 1, 1, 2.0, 1.0,
					GridBagConstraints.EAST, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 5, 5));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == gameSelector.getGameList()) {
			theGame = gameSelector.getSelectedGame();

			for (int i = 0; i < roleLabels.size(); i++) {
				gamePanel.remove(roleLabels.get(i));
				gamePanel.remove(playerFields.get(i));
			}

			roleLabels.clear();
			playerFields.clear();

			validate();
			runButton.setEnabled(false);
			if (theGame == null)
				return;

			StateMachine stateMachine = new ProverStateMachine();
			stateMachine.initialize(theGame.getRules());
			List<Role> roles = stateMachine.getRoles();

			int newRowCount = 10;
			for (int i = 0; i < roles.size(); i++) {
				roleLabels.add(new JLabel(roles.get(i).getName().toString()
						+ ":"));
				playerFields.add(playerSelector.getPlayerSelectorBox());
				playerFields.get(i).setSelectedIndex(
						i % playerFields.get(i).getModel().getSize());

				gamePanel.add(roleLabels.get(i), new GridBagConstraints(0,
						newRowCount, 1, 1, 1.0, 0.0, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(1, 5, 1, 5), 5, 5));
				gamePanel.add(playerFields.get(i), new GridBagConstraints(1,
						newRowCount++, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 5, 1, 5),
						5, 5));
			}
			gamePanel.add(runButton,
					new GridBagConstraints(1, newRowCount, 1, 1, 0.0, 1.0,
							GridBagConstraints.SOUTH,
							GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
									5), 0, 0));

			validate();
			runButton.setEnabled(true);
		}
	}

	private AbstractAction runButtonMethod() {
		return new AbstractAction("Start Experiment!") {

			private static final long serialVersionUID = -5534006812153723920L;

			public void actionPerformed(ActionEvent evt) {
				final int startClock = (Integer) startClockSpinner.getValue();
				final int playClock = (Integer) playClockSpinner.getValue();
				final int numberOfGames = (Integer) numberOfGamesSpinner
						.getValue();
				final List<PlayerPresence> thePlayers = new ArrayList<PlayerPresence>();
				for (JComboBox<String> playerField : playerFields) {
					String name = playerField.getSelectedItem().toString();
					thePlayers.add(playerSelector.getPlayerPresence(name));
				}

				if (shouldSave.isSelected()) {
					File statisticsDir = new File(
							System.getProperty("user.home"),
							"ggp-saved-statistics");
					if (!statisticsDir.exists()) {
						statisticsDir.mkdir();
					}
					String sessionIdPrefix = new String();
					for (PlayerPresence p : thePlayers) {
						if (p != null) {
							sessionIdPrefix += p.getName();
						} else {
							sessionIdPrefix += "unknown player";
						}
						sessionIdPrefix += '.';
					}
					String sessionId = sessionIdPrefix + theGame.getKey() + "."
							+ System.currentTimeMillis();
					File sessionDir = new File(statisticsDir, sessionId);
					if (!sessionDir.exists()) {
						sessionDir.mkdir();
					}
					statisticsPanel.startSavingToDirectory(sessionDir);
				}

				// run the games
				(new Thread(new Runnable() {
					private List<PlayerPresence> players = thePlayers;
					private int startTime = startClock;
					private int playTime = playClock;
					private int numGames = numberOfGames;
					private boolean switchPlayers = switchRoles.isSelected();

					@Override
					public void run() {
						while (numGames > 0) {
							gamesRemainedTextField.setText(Integer
									.toString(numGames));
							startTestingServer(theGame, players, "Base",
									startTime, playTime,
									shouldSave.isSelected(),
									shouldPublish.isSelected());
							--numGames;
							if (numGames == 0) {
								gamesRemainedTextField.setText(NOT_RUNNING);
							}
							if (switchPlayers) {
								PlayerPresence firstPlayer = players.get(0);
								for (int i = 0; i < players.size() - 1; ++i) {
									players.set(i, players.get(i + 1));
								}
								players.set(players.size() - 1, firstPlayer);
							}
						}
						statisticsPanel.saveWhenNecessary();
					}
				})).start();
			}
		};
	}

	private AbstractAction testPlayerButtonMethod() {
		return new AbstractAction("Test") {

			private static final long serialVersionUID = -2800795236609002800L;

			public void actionPerformed(ActionEvent evt) {
				if (playerSelectorList.getSelectedValue() != null) {
					final Game testGame = GameRepository.getDefaultRepository()
							.getGame("maze");
					String playerName = playerSelectorList.getSelectedValue()
							.toString();
					final List<PlayerPresence> thePlayers = Arrays
							.asList(new PlayerPresence[] { playerSelector
									.getPlayerPresence(playerName) });
					(new Thread(new Runnable() {

						@Override
						public void run() {
							startTestingServer(testGame, thePlayers, "Test",
									10, 5, false, false);
						}
					})).start();
				}
			}
		};
	}

	private AbstractAction addPlayerButtonMethod() {
		return new AbstractAction("Add") {

			private static final long serialVersionUID = 1976050741288904029L;

			public void actionPerformed(ActionEvent evt) {
				String hostport = JOptionPane
						.showInputDialog(
								null,
								"What is the new player's address?\nPlease use the format \"host:port\".",
								"Add a player", JOptionPane.QUESTION_MESSAGE,
								null, null, "127.0.0.1:9147").toString();
				try {
					playerSelector.addPlayer(hostport);
				} catch (InvalidHostportException e) {
					JOptionPane.showMessageDialog(null,
							"Could not parse the new player's address! Sorry.",
							"Error adding player", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
	}

	private AbstractAction removePlayerButtonMethod() {
		return new AbstractAction("Remove") {

			private static final long serialVersionUID = 7501607136763989699L;

			public void actionPerformed(ActionEvent evt) {
				if (playerSelectorList.getSelectedValue() != null) {
					playerSelector.removePlayer(playerSelectorList
							.getSelectedValue().toString());
				}
			}
		};
	}

	private void startTestingServer(Game theGame,
			List<PlayerPresence> thePlayers, String matchIdPrefix,
			int startClock, int playClock, boolean shouldSave,
			boolean shouldPublish) {
		try {
			String matchId = matchIdPrefix + "." + theGame.getKey() + "."
					+ System.currentTimeMillis();
			Match match = new Match(matchId, -1, startClock, playClock, theGame);

			List<String> hosts = new ArrayList<String>(thePlayers.size());
			List<Integer> ports = new ArrayList<Integer>(thePlayers.size());
			List<String> playerNames = new ArrayList<String>(thePlayers.size());
			for (PlayerPresence player : thePlayers) {
				hosts.add(player.getHost());
				ports.add(player.getPort());
				playerNames.add(player.getName());
			}

			HistoryPanel historyPanel = new HistoryPanel();
			ErrorPanel errorPanel = new ErrorPanel();
			VisualizationPanel visualizationPanel = new VisualizationPanel(
					theGame);
			StatesPanel statesPanel = new StatesPanel();

			JTabbedPane tab = new JTabbedPane();
			tab.addTab("History", historyPanel);
			tab.addTab("Error", errorPanel);
			tab.addTab("Visualization", visualizationPanel);
			tab.addTab("States", statesPanel);
			JButton closeButton = CloseableTabs.addClosableTab(
					matchesTabbedPane, tab, matchId,
					addTabCloseButton(tab, visualizationPanel));

			match.setCryptographicKeys(signingKeys);
			match.setPlayerNamesFromHost(playerNames);

			GameServer gameServer = new GameServer(match, hosts, ports);
			gameServer.addObserver(errorPanel);
			gameServer.addObserver(historyPanel);
			gameServer.addObserver(visualizationPanel);
			gameServer.addObserver(statesPanel);
			gameServer.addObserver(schedulingPanel);
			gameServer.addObserver(statisticsPanel);
			gameServer.start();

			if (shouldSave) {
				File matchesDir = new File(System.getProperty("user.home"),
						"ggp-saved-matches");
				if (!matchesDir.exists()) {
					matchesDir.mkdir();
				}
				File matchFile = new File(matchesDir, match.getMatchId()
						+ ".json");
				gameServer.startSavingToFilename(matchFile.getAbsolutePath());
			}
			if (shouldPublish) {
				if (!match.getGame().getRepositoryURL().contains("127.0.0.1")) {
					gameServer
							.startPublishingToSpectatorServer("http://matches.ggp.org/");
					gameServer.setForceUsingEntireClock();
				}
			}

			gameServer.join();
			closeButton.doClick();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AbstractAction addTabCloseButton(final Component tabToClose,
			final VisualizationPanel visualizationPanel) {
		return new AbstractAction("x") {

			private static final long serialVersionUID = 846256002308622567L;

			public void actionPerformed(ActionEvent evt) {
				for (int i = 0; i < matchesTabbedPane.getTabCount(); i++) {
					if (tabToClose == matchesTabbedPane.getComponentAt(i)) {
						matchesTabbedPane.remove(tabToClose);
						visualizationPanel.close(); // need to do this to
													// prevent memory leak
					}
				}
			}
		};
	}
}
