package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class GameBot {

	public static final int MIN_PLAYER_COUNT = 20;
	public static final int MAX_PLAYER_COUNT = 100;
	public static final int DRAGON_COUNT = 20;

	public static int playerCount;

	public Server server;
	public Arena arena;
	public Player player;
	public Unit[][] unitCell;

	public boolean gameRunning;

	public GameBot() {
		server = null;
		player = null;
	}

	public void findAndConnectServer(){

		// Pre-defined game coordinator nodes
		ArrayList<Node> masterNodes = new ArrayList<Node>();
		masterNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
		masterNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

		while(server == null) {
			for (Node n : masterNodes) {
				try {
					Registry remoteRegistry = LocateRegistry.getRegistry(n.getHostAddress(), n.getRegistryPort());
					server = (Server) remoteRegistry.lookup(n.getName());
					System.out.println("[System] Connected to Master Server " + n.getFullName() + ".\n");
					break;
				} catch (Exception e) {
					System.out.println("[Error] Unable to connect to game coordinator server " + n.getFullName() + ".\n");
				}
			}

			if(server==null) {
				System.out.println("[System] Retrying connect to server in 5 seconds.\n");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("[System] Welcome to Dragon Arena: Distributed Reborn!\n");
	}

	public void login(String username, String password) {
		System.out.println("[System] Authenticating to server as " + username + "...\n");
		try {
			player = server.login( username, "");
//			syncArena();
		} catch (RemoteException re) {
			re.printStackTrace();
		}

		if(player!=null && player.isAuthenticated()){
			System.out.println("[System] Successfully logged in as " + player.getUsername() + ".\n");
			try {
				Knight knight = new Knight(player.getUsername());
				knight = (Knight) server.spawnUnit(knight);
				player.setUnit(knight);
//				syncArena();
				System.out.println("[Player " + knight.getName() + "] Spawned at coord (" + knight.getX() + "," + knight.getY() + ") of the arena " +
						"with " + knight.getHitPoints() + " HP and " + knight.getAttackPoints() + " AP.\n");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}


	public void syncArena() {

		try {
			arena = server.getArena();
		} catch (RemoteException re) {
			re.printStackTrace();
		}

		unitCell = arena.unitCell;

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
		String[] playerActive = new String[MAX_PLAYER_COUNT];

		// Randomize player count
		playerCount = (int)((MAX_PLAYER_COUNT - MIN_PLAYER_COUNT) * Math.random() + MIN_PLAYER_COUNT);

		for ( int i =0 ; i < playerCount; i++){

			playerActive[i] = "sukma" + i;

			new Knight(playerActive[i]);

		}



	}


}

class ThreadPlayer extends Thread {
	public ThreadPlayer(String str) {
		super(str);
	}

	public void run() {

		GameBot bot = new GameBot();

		// Connect to server
		bot.findAndConnectServer();
		bot.login(getName(), "");

		bot.gameRunning = true;

		Player player = bot.player;
		Server server = bot.server;
		Unit unit = player.getUnit();

		while(GameState.getRunningState() && bot.gameRunning) {
			try {
				/* Sleep while the dragon is considering its next move */
				Thread.currentThread().sleep((int) (unit.getTurnDelay() * 1000 * GameState.GAME_SPEED));

//				Unit adjacentUnit = bot.scanSurrounding(unit);

//				if (adjacentUnit != null){
//					System.out.println(adjacentUnit.getName());
//				}


//				try{
//					if (server.checkSurrounding(unit, unit.getX(), unit.getY() + 1)){
//						player.setUnit(server.moveUnit(unit, unit.getX() , unit.getY() + 1));
//						// Player set to new coordinate
//						System.out.println("[Knight " + player.getUnit().getName() + "] Moved to coord (" + player.getUnit().getX() + "," + player.getUnit().getY() + ") of the arena.\n");
//
//					} else { // There are unit, Dragon or Knight
//						Unit adjacentunit = server.actionToSurroundingUnit(player.getUnit(), player.getUnit().getX(), player.getUnit().getY()+ 1);
//						if (adjacentunit.getType().equals("dragon")){
//							// Damage dragon
//							System.out.println("[Knight " + player.getUnit().getName() + "] damage " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
//							System.out.println("[" + adjacentunit.getName() + "] damaged to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
//						} else { // Knight
//							// Heal player
//							System.out.println("[Knight " + player.getUnit().getName() + "] heal " + adjacentunit.getName() + " by " + player.getUnit().getAttackPoints() + ".\n");
//							System.out.println("[Knight " + adjacentunit.getName() + "] healed to " + adjacentunit.getHitPoints() + "/" + adjacentunit.getMaxHitPoints() + " HP.\n");
//						}
//						// Check whether unit dead after damage
//						// Case1: Knight attack Dragon
//						// Case2: Dragon attack Knight
//						// Delete unit
//						if (server.checkDead(adjacentunit)) {
//							server.deleteUnit(adjacentunit);
//
//						}
//					}
//				 } catch (RemoteException re) {
//					re.printStackTrace();
//				 }


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
