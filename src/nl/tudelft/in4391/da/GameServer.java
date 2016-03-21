package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
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
            node = new Node(nodeID,InetAddress.getLocalHost().getHostAddress(), registry_port, callback_port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Init Server Object
        final ServerImpl server = new ServerImpl(node);

        // Create New Registry
        try {
            LocateRegistry.createRegistry(registry_port);
        } catch (RemoteException e) {
            System.err.println("[System] Error Exception: " + e.toString());
            e.printStackTrace();
        }

        // Get Registry
        Registry registry = null;
        Server stub = null;
        try {

            // Get Local Registry
            registry = LocateRegistry.getRegistry(registry_port);

            // Stub and Skeleton
            stub = (Server) UnicastRemoteObject.exportObject(server,callback_port);
            registry.bind(node.getName(), stub);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Multicast multicast = null;

        try {
            // Init multicast object and join Muticast group
            multicast = new Multicast(Node.serialize(node), DEFAULT_MULTICAST_GROUP, socket_port);
            multicast.setListener(new MulticastListener() {
                @Override
                public void onReceiveData(byte[] receiveData, int length) {
                    Node receiveNode = null;
                    try {
                        receiveNode = Node.deserialize(receiveData);
                        server.addActiveNode(receiveNode);
                        System.out.println("[System] " + receiveNode.getFullName() + " is active.");

                        if(!nodeID.equals(receiveNode.getID())) { // if the multicast not from himself
                            Server remoteComponent = ServerImpl.fromRemoteNode(receiveNode);
                            remoteComponent.register(new Node(nodeID, InetAddress.getLocalHost().getHostAddress(), registry_port, callback_port));
                            //System.out.println("[System] Register current node status on " + receiveNode.getName()+".");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Run listener thread
            multicast.start();

            // Multicast that this node is active
            multicast.send();

        } catch (IOException e) {
            e.printStackTrace();
        }




        String command = "";

        while(!command.equals("exit")){
            command = s.nextLine().trim();
            switch(command) {
                case "nodes":
                    System.out.println("\n[Active Nodes: "+server.getActiveNodes().size()+"]");
                    for(Node n : server.getActiveNodes()) {
                        System.out.println("- "+n.getFullName());
                    }
                    break;

                case "players":
                    System.out.println("\n[Active Players: "+server.getActivePlayers().size()+"]");
                    for(Player p : server.getActivePlayers()) {
                        System.out.println("- "+p.toString());
                    }
                    break;
                default:
                    break;
            }

        }
    }
}
