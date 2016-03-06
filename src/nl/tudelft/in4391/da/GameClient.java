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
    static String DEFAULT_HOST = "localhost";
    static Integer DEFAULT_CALLBACK_PORT = 1201;

    public static void main(String[] args)
    {
        // Parameter Arguments
        String server_host = (args.length < 1) ? DEFAULT_HOST : args[0];
        Integer server_port = (args.length < 2) ? GameServer.DEFAULT_PORT : Integer.parseInt(args[1]); // 1101 ~ ...
        Integer callback_port = (args.length < 3) ? DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]); // 1201 ~ ...

        Scanner s = new Scanner(System.in);

        try {
            // Init server connection
            Registry serverRegistry = LocateRegistry.getRegistry(server_host,server_port);
            Server server = (Server) serverRegistry.lookup("Server");

            // Lookup Server whether the server is available or not
            if(server.connect()) {
                System.out.println("[System] Connected to game server "+server_host+":"+server_port);
                System.out.println("Welcome to Dragon Arena: Distributed Reborn!");

                // Init client object & register client
                ClientImpl client = new ClientImpl();

                // Register local client stub
                Client clientStub = (Client) UnicastRemoteObject.exportObject(client, callback_port);
                Registry clientRegistry = LocateRegistry.getRegistry();
                clientRegistry.bind("Client-" + UUID.randomUUID(), clientStub);


                // Authentication Logic
                String username = "";
                String password = "";
                Player player;

                do {
                    // Input authentication credentials
                    System.out.print("Username: ");
                    username = s.nextLine().trim();

                    // TO-DO: Implement password authentication
                    //System.out.println("Password: ");
                    //password = s.nextLine().trim();

                    System.out.println("[System] Authenticating to server...");
                    player = server.login(username,password);
                } while(!player.isAuthenticated());
                System.out.println("[System] Successfully authenticated to server as "+player.toString()+".");

            } else {
                System.out.println("[Error] Couldn't able to reach game server "+server_host+":"+server_port+". Please try again later.");
                return; // exit
            }



        } catch (Exception e) {
            System.err.println("[Error] Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
