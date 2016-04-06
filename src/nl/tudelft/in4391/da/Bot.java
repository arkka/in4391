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
    private static Integer GAME_SPEED = 100; //ms
    private static Integer TURN_DELAY = 500;

    public ArrayList<Node> serverNodes;

    public static final int MIN_PLAYER_COUNT = 2;
    public static final int MAX_PLAYER_COUNT = 100;
    public static final int DRAGON_COUNT = 20;

    public static int playerCount;

    public Server server;
    public Arena arena;
    public Player player;
    public Unit[][] unitCell;

    public boolean gameRunning;

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

    // Up to total 2 distance
    public Unit scanSurrounding(Unit unit) {
        Unit adjacentUnit;
        Integer sourceX = unit.getX();
        Integer sourceY = unit.getY();

        // Random surrounding
        Integer x = rand.nextInt(1 + 1 + 1) - 1;
        Integer y = rand.nextInt(1 + 1 + 1) - 1;

        adjacentUnit = unitCell[sourceX + x][sourceY + y];

        if (adjacentUnit != null){
            return adjacentUnit;
        } else {
            adjacentUnit = null;
        }

//        scanUnit:
//        for (int i = 0 ; i < 11 ; i++){
//            // Get the nearby unit for at most 2 distance
//            switch (i) {
//                case 1:
//                    // Get unit on the right
//                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY()];
//                    break;
//                case 2:
//                    // Get unit on the left
//                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY()];
//                    break;
//                case 3:
//                    // Get unit on the top
//                    adjacentUnit = unitCell[unit.getX()][unit.getY() + 1];
//                    break;
//                case 4:
//                    // Get unit on the bottom
//                    adjacentUnit = unitCell[unit.getX()][unit.getY() - 1];
//                    break;
//                case 5:
//                    // Get unit on the diagonal top left
//                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY() + 1];
//                    break;
//                case 6:
//                    // Get unit on the diagonal top right
//                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY() + 1];
//                    break;
//                case 7:
//                    // Get unit on the diagonal bottom left
//                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY() - 1];
//                    break;
//                case 8:
//                    // Get unit on the diagonal bottom right
//                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY() - 1];
//                    break;
//                case 9:
//                    // Get unit on the right distance 2
//                    adjacentUnit = unitCell[unit.getX() + 2][unit.getY()];
//                    break;
//                case 10:
//                    // Get unit on the left distance 2
//                    adjacentUnit = unitCell[unit.getX() - 2][unit.getY()];
//                    break;
//                case 11:
//                    // Get unit on the top distance 2
//                    adjacentUnit = unitCell[unit.getX()][unit.getY() + 2];
//                    break;
//                case 12:
//                    // Get unit on the bottom distance 2
//                    adjacentUnit = unitCell[unit.getX()][unit.getY() - 2];
//                    break;
//            }
//
//            if (adjacentUnit != null){
//                break scanUnit;
//            }
//
//        }

        return adjacentUnit;
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


    public void run() {
        gameRunning = true;
        try {
            while (gameRunning && GameState.getRunningState()){

                Thread.sleep(GAME_SPEED + TURN_DELAY);

                arena = server.getArena();
                arena.syncUnits();
                player.setUnit(arena.getMyUnit(player));

                Unit unit = player.getUnit();

                // Unit dead
                if (unit.getHitPoints() <= 0)
                    break;

                // Get surrounding
                Integer x = rand.nextInt(1 + 1 + 1) - 1;
                Integer y = rand.nextInt(1 + 1 + 1) - 1;

                if (unit instanceof Dragon) {

                } else { // Knight
                    Unit adjacentUnit = scanSurrounding(unit);

                    if (adjacentUnit == null){
                        server.moveUnit(unit, adjacentUnit.getX(), adjacentUnit.getY());
                    } else {
                        server.actionUnit(unit, adjacentUnit);
                    }
                }


            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
