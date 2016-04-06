package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Random;


public class GameBot {

	private static Integer GAME_SPEED = 100; //ms

	public ArrayList<Node> serverNodes;

	public static final int MIN_PLAYER_COUNT = 2;
	public static final int MAX_PLAYER_COUNT = 100;
	public static final int DRAGON_COUNT = 20;

	public static int playerCount;

	public Server server;
	public Arena arena;
	public Player player;
	public Unit[][] unitCell;

	public JTextArea console;
	private JPanel arenaPanel;

	public boolean gameRunning;

	public GameBot() {
		// Server Nodes
		serverNodes = new ArrayList<Node>();
		serverNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
		serverNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

		// Server object based on latency
		server = findServer();
		player = null;
		arena = new Arena();

	}

	public Server findServer() {
		Server bestServer = null;
		Node bestNode = null;

		long t = 0;
		long latency = 0;
		long maxLatency = 10000; // 10 seconds
		long bestLatency = maxLatency;

		// Ping all server and find the best latency
		for (Node n : serverNodes) {
			Server s = ServerImpl.fromRemoteNode(n);
			if(s!=null) {
				t = System.currentTimeMillis();

				try {
					if (s.ping()) {
						latency = System.currentTimeMillis() - t;
						consoleLog("[System] Game server " + n.getFullName() + " is available. ("+ latency +"ms)");
					}
				} catch (RemoteException e) {
					//e.printStackTrace();
					latency = maxLatency;
					consoleLog("[System] Game server " + n.getFullName() + " is down.");
				}
				n.setLatency(latency);

				if (latency < bestLatency) {
					bestLatency = latency;
					bestServer = s;
					bestNode = n;
				}
			}
		}
		if(bestServer!=null)
			consoleLog("[System] Connected to Game Server " + bestNode.getFullName() + ". ("+ bestLatency +"ms)");
		else
			consoleLog("[System] No available game server. Please try again later.");

		return bestServer;
	}

	public void login(String username, String password) {
		consoleLog("[System] Authenticating to server as `"+ username +"`...");
		if (server==null) findServer();
		try {
			player = server.login( username, "");
			if(player!=null) {
				consoleLog("[System] Successfully logged in as `"+ username +"`");
				updateArena();
			}
		} catch (RemoteException re) {
			re.printStackTrace();
			consoleLog("[System] Authentication as " + username + " failed.");
		}
	}

	public void logout() {
		try {
			server.logout(player);
			server = null;
			player = null;
			arena = new Arena();
			renderArena();

			consoleLog("[System] Successfully logged out from server.");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void updateArena() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(server!=null) {
						//consoleLog("[System] Sync arena map.");
						arena = server.getArena();
						renderArena();
						Thread.sleep(GAME_SPEED);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// GUI purpose
	public void renderArena() {
		Component[] components = arenaPanel.getComponents();

		int cellIndex = 0;
		for(int j=24;j>0;j--) {
			for(int i=0;i<25;i++) {
				Component component = components[cellIndex];
				if (component instanceof JLabel)
				{
					Unit unit = arena.unitCell[i][j];
					if(unit!=null) {
						if(unit.getType().equals("Dragon"))  ((JLabel) component).setText("D");
						else  ((JLabel) component).setText("K");
					} else {
						((JLabel) component).setText(" ");
					}
				}
				cellIndex++;
			}
		}
	}

	public void consoleLog(String message) {
		console.append(message);
		console.append("\n");
		console.setCaretPosition(console.getDocument().getLength());
	}

	// Scan for unit nearby
	// Up to total 2 distance
	public Unit scanSurrounding(Unit unit) {
		Unit adjacentUnit = unitCell[unit.getX()][unit.getY()];

		scanUnit:
		for (int i = 0 ; i < 11 ; i++){
			// Get the nearby unit for at most 2 distance
			switch (i) {
				case 1:
					// Get unit on the right
					adjacentUnit = unitCell[unit.getX() + 1][unit.getY()];
					break;
				case 2:
					// Get unit on the left
					adjacentUnit = unitCell[unit.getX() - 1][unit.getY()];
					break;
				case 3:
					// Get unit on the top
					adjacentUnit = unitCell[unit.getX()][unit.getY() + 1];
					break;
				case 4:
					// Get unit on the bottom
					adjacentUnit = unitCell[unit.getX()][unit.getY() - 1];
					break;
				case 5:
					// Get unit on the diagonal top left
					adjacentUnit = unitCell[unit.getX() - 1][unit.getY() + 1];
					break;
				case 6:
					// Get unit on the diagonal top right
					adjacentUnit = unitCell[unit.getX() + 1][unit.getY() + 1];
					break;
				case 7:
					// Get unit on the diagonal bottom left
					adjacentUnit = unitCell[unit.getX() - 1][unit.getY() - 1];
					break;
				case 8:
					// Get unit on the diagonal bottom right
					adjacentUnit = unitCell[unit.getX() + 1][unit.getY() - 1];
					break;
				case 9:
					// Get unit on the right distance 2
					adjacentUnit = unitCell[unit.getX() + 2][unit.getY()];
					break;
				case 10:
					// Get unit on the left distance 2
					adjacentUnit = unitCell[unit.getX() - 2][unit.getY()];
					break;
				case 11:
					// Get unit on the top distance 2
					adjacentUnit = unitCell[unit.getX()][unit.getY() + 2];
					break;
				case 12:
					// Get unit on the bottom distance 2
					adjacentUnit = unitCell[unit.getX()][unit.getY() - 2];
					break;
			}

			if (adjacentUnit != null){
				break scanUnit;
			}

		}

		return adjacentUnit;
	}

	public static void main(String[] args)
	{
		GameBot bot = new GameBot();
		Random rand = new Random();

		try {
			bot.server.login("a","");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		boolean run = true;



			try {
				while (run){
					bot.arena = bot.server.getArena();
//					bot.arena.syncUnits();
					bot.player.setUnit(bot.arena.getMyUnit(bot.player));


					Integer x = rand.nextInt(3) + 1;
					Integer y = rand.nextInt(3) + 1;

					bot.server.moveUnit(bot.player.getUnit(), x, y);

					Thread.sleep(GAME_SPEED);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		// Randomize player count
//		playerCount = (int)((MAX_PLAYER_COUNT - MIN_PLAYER_COUNT) * Math.random() + MIN_PLAYER_COUNT);
//		for ( int i =0 ; i < MIN_PLAYER_COUNT; i++){
//			new Thread(new Runnable() {
//				public void run() {
//
//				}
//			}).start();
//			final int nameInt = i;
//			String username = "sukma" + i;
//		}


	}


}