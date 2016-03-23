package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Unit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameServer {
    static Integer DEFAULT_NODE_ID = 1;
    static Integer DEFAULT_REGISTRY_PORT = 1100;
    static Integer DEFAULT_CALLBACK_PORT = 1200;
    static Integer DEFAULT_SOCKET_PORT = 1300;
    static String DEFAULT_MULTICAST_GROUP = "239.255.0.113";

    public static void main (String[] args) {
        // Parameter Arguments
        final Integer nodeID = (args.length < 1) ? DEFAULT_NODE_ID : Integer.parseInt(args[0]);
        final Integer registry_port = (args.length < 2) ? DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]);
        final Integer callback_port = (args.length < 3) ? DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]);
        final Integer socket_port = (args.length < 4) ? DEFAULT_SOCKET_PORT : Integer.parseInt(args[3]);

        // Input Scanner
        Scanner s = new Scanner(System.in);

        // Init Node information
        Node node = null;
        try {
            node = new Node(nodeID,InetAddress.getLocalHost().getHostAddress(), registry_port, callback_port, socket_port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Init Server Object
        final ServerImpl server = new ServerImpl(node);


        // COMMAND
        String command = "";

        while(true){
            command = s.nextLine().trim();
            switch(command) {
                case "node":
                case "nodes":
                    System.out.println("\n[Active Nodes: "+server.getActiveNodes().size()+"]");

                    System.out.println(" Master");
                    for(Node n : server.getMasterNodes()) {
                        System.out.println("- "+n.getFullName());
                    }

                    System.out.println(" Worker");
                    for(Node n : server.getWorkerNodes()) {
                        System.out.println("- "+n.getFullName());
                    }
                    break;

                case "player":
                case "players":
                    System.out.println("\n[Active Players: "+server.getActivePlayers().size()+"]");
                    for(Player p : server.getActivePlayers()) {
                        System.out.println("- "+p.toString());
                    }
                    break;

                case "dragon":
                case "dragons":
                    System.out.println("\n[Active Dragons: "+server.getArena().getDragons().size()+"]");
                    for(Unit d : server.getArena().getDragons()) {
                        System.out.println("- "+d.getName());
                    }
                    break;

                case "release dragon":
                case "release dragons":
                    server.releaseDragons(25);
                    break;

                case "show arena":
                case "arena":
                    server.getArena().show();
                    break;

                case "exit":
                    server.shutdown();
                    System.exit(0);
                    break;
                default:
                    break;
            }

        }
    }
}
