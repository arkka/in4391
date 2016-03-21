package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClient {
    public static Integer CLIENT_CALLBACK_PORT = 1500;

    public static void main(String[] args)
    {
        // Pre-defined game coordinator nodes
        ArrayList<Node> activeNodes = new ArrayList<Node>();
        activeNodes.add(new Node(1,"145.94.144.66",1100,1200));
        activeNodes.add(new Node(2,"145.94.144.66",1101,1201));

        // Try to connect to known game coordinator nodes
        Registry remoteRegistry = null;
        Server server= null;
        Node connectedNode = null;
        for (Node n: activeNodes) {
            try {
                remoteRegistry = LocateRegistry.getRegistry(n.getHostAddress(),n.getRegistryPort());
                server = (Server) remoteRegistry.lookup(n.getName());
                if(server.connect()) {
                    System.out.println("[System] Connected to Game Coordinator "+n.getFullName());
                    System.out.println("[System] Welcome to Dragon Arena: Distributed Reborn!");
                    connectedNode = n;
                    break;
                }
            } catch (Exception e) {
                System.out.println("[Error] Failed connection to Game Coordinator "+n.getFullName()+". Trying another Game Coordinator nodes...");
            }
        }

        // Login
        Scanner s = new Scanner(System.in);
        Player player = null;
        String username = "";
        String password = "";

        do {
            // Input authentication credentials
            System.out.print("Username: ");
            username = s.nextLine().trim();

            // TO-DO: Implement password authentication
            //System.out.println("Password: ");
            //password = s.nextLine().trim();

            System.out.println("[System] Authenticating to server as "+username+" ...");
            try {
                player = server.login(username,password);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } while(!player.isAuthenticated());
    }
}
