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
public class EventDispatcher extends Thread {
    private static final double GAME_SPEED = .1; //ms
    private static Integer TURN_DELAY = 500;

    public ArrayList<Node> serverNodes;

    public Server server;
    public Arena arena;

    public boolean running;

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

    public EventDispatcher() {
        serverNodes = new ArrayList<Node>();
        serverNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
        serverNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

        // Server object based on latency
        server = findServer();
        arena = new Arena();


        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("[System] Dispatcher service terminated...");
                System.out.println("Bye!");
            }
        });
    }

    public void run() {
        running = true;
        /*
        try {
            while (running){
                Thread.currentThread().sleep((int)(GAME_SPEED * TURN_DELAY));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
}
