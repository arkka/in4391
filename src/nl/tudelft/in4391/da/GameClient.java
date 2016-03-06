package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.UUID;

public class GameClient {
    static String DEFAULT_SERVER_REGISTRY_HOST = "127.0.0.1";
    static String DEFAULT_ARENA_REGISTRY_HOST = "127.0.0.1";

    public static void main(String[] args)
    {
        // Parameter Arguments
        String server_host = (args.length < 1) ? DEFAULT_SERVER_REGISTRY_HOST : args[0];
        Integer server_port = (args.length < 2) ? GameServer.DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]); // 1100 ~ ... on localhost
        Integer callback_port = (args.length < 3) ? GameServer.DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]); // 1500 ~ ... on localhost

        // Input Scanner
        Scanner s = new Scanner(System.in);

        Player player;

        try {
            // Get Registry
            Registry clientRegistry = LocateRegistry.getRegistry();
            Registry serverRegistry = LocateRegistry.getRegistry(server_host,server_port);
            Registry arenaRegistry = LocateRegistry.getRegistry(server_host,server_port);
            Server server = (Server) serverRegistry.lookup("Server");

            // Lookup Server and Authenticate
            if(server.connect()) {
                System.out.println("[System] Connected to game server "+server_host+":"+server_port);
                System.out.println("Welcome to Dragon Arena: Distributed Reborn!");

                // Authentication Logic
                String username = "";
                String password = "";


                do {
                    // Input authentication credentials
                    System.out.print("Username: ");
                    username = s.nextLine().trim();

                    // TO-DO: Implement password authentication
                    //System.out.println("Password: ");
                    //password = s.nextLine().trim();

                    System.out.println("[System] Authenticating to server...");
                } while(!server.login(username,password));

                player = server.getPlayer();

                // Register local client stub
                Client clientStub = (Client) UnicastRemoteObject.exportObject(player, callback_port);
                clientRegistry.bind("Client-" + UUID.randomUUID(), clientStub);

                System.out.println("[System] Successfully authenticated to server as "+player.toString()+".");

            } else {
                System.out.println("[Error] Couldn't able to reach game server "+server_host+":"+server_port+". Please try again later.");
                return; // exit
            }

            // Join Arena Server

            System.out.println("\nDEBUG");
            System.out.println("[Server Registry]");
            for(String sr : serverRegistry.list()) {
                System.out.println("- "+sr);
            }

            System.out.println("\n[Client Registry]");
            for(String cr : clientRegistry.list()) {
                System.out.println("- "+cr);
            }




        } catch (Exception e) {
            System.err.println("[Error] Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
