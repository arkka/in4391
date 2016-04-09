package nl.tudelft.in4391.da;

import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class GameServer {
    public static void main(String[] args) {
        // Parameter Arguments
        final Integer nodeId = (args.length < 1) ? Node.DEFAULT_NODE_ID : Integer.parseInt(args[0]);
        final Integer registry_port = (args.length < 2) ? Node.DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]);
        final Integer callback_port = (args.length < 3) ? Node.DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]);
        final Integer socket_port = (args.length < 4) ? Node.DEFAULT_SOCKET_PORT : Integer.parseInt(args[3]);
        final Integer type = (args.length < 5) ? Node.TYPE_MASTER : Integer.parseInt(args[4]);

        // Initialize Current Node
        Node currentNode = new Node(nodeId,registry_port,callback_port,socket_port);
        currentNode.setType(type);

        // Initialize server object
        ServerImpl server = new ServerImpl(currentNode);

        // Input
        Scanner s = new Scanner(System.in);

        // Command
        String command = "";

        while(true) {
            command = s.nextLine().trim();
            switch (command) {
                case "node":
                case "nodes":
                    //System.out.println("\n[Master Nodes: " + server.getNodes().size() + "]");
                    System.out.println("\n[Master Nodes: ]");
                    for (Node n : server.getMasterNodes()) {
                        System.out.println("- " + n.getFullName());
                    }

                    //System.out.println("\n[Worker Nodes: " + server.getNodes().size() + "]");
                    System.out.println("\n[Worker Nodes: ]");
                    for (Node n : server.getWorkerNodes()) {
                        System.out.println("- " + n.getFullName());
                    }
                    break;

                case "player":
                case "players":
                    System.out.println("\n[Players: " + server.getPlayers().size() + "]");
                    for (Player p : server.getPlayers()) {
                        System.out.println("- " + p.getUsername());
                    }
                    break;

                case "show arena":
                case "arena":
                    server.getArena().show();
                    break;

                case "event":
                case "events":
                case "queue":
                    System.out.println("\n[Event Queue: " + server.getEventQueue().size() + "]");
                    for (Object o : server.getEventQueue()) {
                        EventMessage em = (EventMessage) o;
                        System.out.println("- " + em.getCode());
                    }
                    break;
                case "dispatch":
                    server.eventDispatcher();
                    break;
                case "exit":
                    System.exit(-1);
            }
        }

    }
}
