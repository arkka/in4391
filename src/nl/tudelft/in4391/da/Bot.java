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

    public Unit adjacentUnit;

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

		// Random surrounding 2 vertical horizontal
		// Range (-2,2)
		Integer x2 = rand.nextInt(4) - 2;
		Integer y2 = rand.nextInt(4) - 2;

		foundUnit = false;

		try {
			// Get adjacent unit
			// When not pointing to itself
			// When not out of bound
			while (!foundUnit && (x != 0 && y != 0 ) && (sourceX + x < 25 || sourceX + x >= 0) && (sourceY + y < 25 || sourceY + y >= 0) ){
				adjacentUnit = arena.unitCell[sourceX + x][sourceY + y];

                if (adjacentUnit == null){ //Check horizontal and vertical 2 square
                    Integer direction = rand.nextInt(2);
                    switch(direction){
                        case 0:
                            //horizontal
                            adjacentUnit = arena.unitCell[sourceX + x2][sourceY];
                            break;
                        case 1:
                            //vertical
                            adjacentUnit = arena.unitCell[sourceX][sourceY + y2];
                            break;
                    }

                    if (adjacentUnit == null && unit instanceof Knight){
                        // Random surrounding 2 vertical horizontal
                        // Range (-2,2)
                        Integer x5 = rand.nextInt(10) - 5;
                        Integer y5 = rand.nextInt(10) - 5;

                        Integer direction2 = rand.nextInt(2);
                        switch(direction2){
                            case 0:
                                //horizontal
                                adjacentUnit = arena.unitCell[sourceX + x5][sourceY];
                                break;
                            case 1:
                                //vertical
                                adjacentUnit = arena.unitCell[sourceX][sourceY + y5];
                                break;
                        }
                    }

                }
				foundUnit = true;
			}

		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("Array is out of Bounds"+e);
//			System.out.println("Scan other direction");
			x = rand.nextInt(3) - 1;
			y = rand.nextInt(3) - 1;
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

                if (player.getUnit() == null){
                    break;
                }

                Unit unit = player.getUnit();

	            // Random surrounding
	            // Range (-1,1)
	            Integer x = rand.nextInt(3) - 1;
	            Integer y = rand.nextInt(3) - 1;

                if (unit instanceof Dragon) {
                    adjacentUnit = scanSurrounding(unit, arena);

                    if (adjacentUnit != null && adjacentUnit instanceof Knight ){
                        server.attackUnit(unit, adjacentUnit);
                    }

                } else { // Knight
                    adjacentUnit = scanSurrounding(unit, arena);

                    if (adjacentUnit == null ){
	                    // Check whether random 0 for both axis
	                    // Move accordingly
	                    if (x == 0 || y == 0) {
		                    server.moveUnit(unit, unit.getX() + x , unit.getY() + y);
	                    } else {
		                    // Move horizontally or vertically 1 block
		                    // When random value is not zero for x y
		                    Integer direction = rand.nextInt(2);
		                    switch(direction){
			                    case 0:
				                    //horizontal
				                    server.moveUnit(unit, unit.getX() + x , unit.getY());
				                    break;
			                    case 1:
				                    //vertical
				                    server.moveUnit(unit, unit.getX(), unit.getY() + y);
				                    break;
		                    }
	                    }

                    } else { // Adjacent Unit exists
	                    // Do action
                        if (adjacentUnit instanceof Dragon && (  Math.abs(unit.getX() - adjacentUnit.getX()) <= 2 && Math.abs(unit.getY() - adjacentUnit.getY()) <= 2  )){
                            server.attackUnit(unit, adjacentUnit);
                        } else {
                            if ( unit.getX() - adjacentUnit.getX() != 0 && unit.getY() - adjacentUnit.getY() != 0) {
                                if (adjacentUnit.getHitPoints() <= 0.5 * adjacentUnit.getMaxHitPoints() ){
                                    server.healUnit(unit, adjacentUnit);
                                }

                            }
                        }
                    }
                }

                gameRunning = unit.running;

	            Thread.currentThread().sleep((int)(unit.getTurnDelay() * GAME_SPEED * TURN_DELAY));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
