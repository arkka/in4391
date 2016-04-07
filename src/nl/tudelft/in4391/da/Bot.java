package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arkkadhiratara on 4/6/16.
 */
public class Bot extends Thread {
    private static final double GAME_SPEED = .1; //ms
    private static Integer TURN_DELAY = 500;

    public ArrayList<Node> serverNodes;

    public Server server;
    public Arena arena;
    public Player player;

    public boolean gameRunning;
	public boolean foundUnit;

    Random rand = new Random();

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
                        System.out.println("[System] Game server " + n.getFullName() + " is available. ("+ latency +"ms)");
                    }
                } catch (RemoteException e) {
                    //e.printStackTrace();
                    latency = maxLatency;
                    System.out.println("[System] Game server " + n.getFullName() + " is down.");
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
            System.out.println("[System] Connected to Game Server " + bestNode.getFullName() + ". ("+ bestLatency +"ms)");
        else
            System.out.println("[System] No available game server. Please try again later.");

        return bestServer;
    }

    public Bot(String username, String type) {
        serverNodes = new ArrayList<Node>();
        serverNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
        serverNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

        // Server object based on latency
        server = findServer();
        player = null;
        arena = new Arena();

        try {
            player = server.login(username,"",type);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

	// Up to total 2 distance
	public Unit scanSurrounding(Unit unit, Arena arena) {
		Unit adjacentUnit = null;

		Integer sourceX = unit.getX();
		Integer sourceY = unit.getY();

		// Random surrounding
		// Range (-1,1)
		Integer x = rand.nextInt(3) - 1;
		Integer y = rand.nextInt(3) - 1;

		// Random surrounding 2
		// Range (-2,2)
//		Integer x2 = rand.nextInt(4) - 2;
//		Integer y2 = rand.nextInt(4) - 2;

		foundUnit = false;

		try {
			// Get adjacent unit
			// When not pointing to itself
			// When not out of bound
			while (!foundUnit && (x != 0 && y != 0 ) && (sourceX + x < 25 || sourceX + x >= 0) && (sourceY + y < 25 || sourceY + y >= 0) ){
				adjacentUnit = arena.unitCell[sourceX + x][sourceY + y];
				foundUnit = true;
			}

		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("Array is out of Bounds"+e);
//			System.out.println("Scan other direction");
			x = rand.nextInt(1 + 1 + 1) - 1;
			y = rand.nextInt(1 + 1 + 1) - 1;
			foundUnit = false;
		}

		return adjacentUnit;

	}


	public void run() {
        gameRunning = true;
        try {
            while (gameRunning && GameState.getRunningState()){

                arena = server.getArena();
                arena.syncUnits();
                player.setUnit(arena.getMyUnit(player));

                Unit unit = player.getUnit();

                // Unit dead
                if (unit.getHitPoints() <= 0)
                    break;

	            // Random surrounding
	            // Range (-1,1)
	            Integer x = rand.nextInt(3) - 1;
	            Integer y = rand.nextInt(3) - 1;

                if (unit instanceof Dragon) {

                } else { // Knight
                    Unit adjacentUnit = scanSurrounding(unit, arena);

                    if (adjacentUnit == null ){
	                    // Check whether random 0 for both axis
	                    // Move accordingly
	                    if (x == 0 || y == 0) {
		                    server.moveUnit(unit, unit.getX() + x , unit.getY() + y);
		                    System.out.println("test 1");
	                    } else{
		                    // Move horizontally or vertically 1 block
		                    // When random value is not zero for x y
		                    Integer direction = rand.nextInt(2);
		                    switch(direction){
			                    case 0:
				                    //horizontal
				                    server.moveUnit(unit, unit.getX() + x , unit.getY());
				                    System.out.println("horizontal");
				                    break;
			                    case 1:
				                    //vertical
				                    server.moveUnit(unit, unit.getX(), unit.getY() + y);
				                    System.out.println("vertical");
				                    break;
		                    }
	                    }

                    } else {
	                    // Adjacent Unit detected
	                    // Do action
                        server.actionUnit(unit, adjacentUnit);
	                    System.out.println("ACTIONNNN");
                    }
                }

	            Thread.currentThread().sleep((int)(unit.getTurnDelay() * GAME_SPEED * TURN_DELAY));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
