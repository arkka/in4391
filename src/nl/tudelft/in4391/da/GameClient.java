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

        try {
            // Init server connection
            Registry serverRegistry = LocateRegistry.getRegistry(server_host,server_port);
            Server server = (Server) serverRegistry.lookup("Server");

            // Lookup Server whether the server is available or not
            if(server.ping()) {
                System.out.println("[System] Connected to game server "+server_host+":"+server_port);
                System.out.println("Welcome to Dragon Arena: Reborn!");

                // Input authentication credentials
                Scanner s = new Scanner(System.in);
                System.out.print("Username: ");
                String username = s.nextLine().trim();


                // TO-DO: Implement password authentication
                //System.out.println("Password: ");
                //password = s.nextLine().trim();
                String password = "";

                // Init client object
                ClientImpl client = new ClientImpl(username,password);

                // Register local client stub
                Client clientStub = (Client) UnicastRemoteObject.exportObject(client, callback_port);
                Registry clientRegistry = LocateRegistry.getRegistry();
                clientRegistry.bind("Client-" + UUID.randomUUID(), clientStub);

                System.out.println("[System] Authenticating to server...");
                server.login(client);

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
