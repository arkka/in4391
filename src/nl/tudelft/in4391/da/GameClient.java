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
            // Username Input
            Scanner s = new Scanner(System.in);
            System.out.println("Username: ");
            String username = s.nextLine().trim();

            // Init Client Object
            ClientImpl client = new ClientImpl(username);

            // Register Client Stub
            Client clientStub = (Client) UnicastRemoteObject.exportObject(client, callback_port);
            Registry clientRegistry = LocateRegistry.getRegistry();
            clientRegistry.bind("Client-" + UUID.randomUUID(), clientStub);
            System.out.println("[System] Client is ready.");

            // Lookup Server Stub
            Registry serverRegistry = LocateRegistry.getRegistry(server_host,server_port);
            Server serverStub = (Server) serverRegistry.lookup("Server");

            // Set client information
            serverStub.connect(client);

            // Ping server
            // serverStub.ping();

        } catch (Exception e) {
            System.err.println("[Error] Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}
