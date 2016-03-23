package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import nl.tudelft.in4391.da.unit.Knight;

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
        ArrayList<Node> masterNodes = new ArrayList<Node>();
        masterNodes.add(new Node(1,"127.0.0.1",1100,1200));
        masterNodes.add(new Node(2,"127.0.0.1",1101,1201));

        // Try to connect to known game coordinator nodes
        Server server = findMasterAndConnect(masterNodes);
        System.out.println("[System] Welcome to Dragon Arena: Distributed Reborn!");


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

            System.out.println("[System] Authenticating to server as "+username+"...");
            try {
                player = server.login(username,password);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } while(!player.isAuthenticated());

        if(player!=null && player.isAuthenticated()){
            System.out.println("[System] Successfully logged in as "+player.getUsername()+".");
            try {
                Knight knight = (Knight) server.spawnUnit(player);
                System.out.println("[System] Your Knight" + knight.getName() + " is spawned at coord (" + knight.getX() + "," + knight.getY() + ") of the arena.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

        // COMMAND
        String command = "";

        while(true){
            command = s.nextLine().trim();
            switch(command) {
                case "up":
                    break;
                case "down":
                    break;
                case "left":
                    break;
                case "right":
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    break;
            }

        }
    }

    private static Server findMasterAndConnect(ArrayList<Node> masterNodes) {
        Server server = null;
        for (Node n: masterNodes) {
            try {
                Registry remoteRegistry = LocateRegistry.getRegistry(n.getHostAddress(),n.getRegistryPort());
                server = (Server) remoteRegistry.lookup(n.getName());
                System.out.println("[System] Connected to game coordinator server "+n.getFullName()+".");
                break;
            } catch (Exception e) {
                System.out.println("[Error] Unable to connect to game coordinator server "+n.getFullName()+".");
            }
        }

        if(server==null) {
            System.out.println("[System] Retrying connect to server in 5 seconds.");

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            server = findMasterAndConnect(masterNodes);
        }

        return server;
    }
}