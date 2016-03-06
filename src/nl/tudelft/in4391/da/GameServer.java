package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GameServer {
    static String SERVER_REGISTRY_HOST = "127.0.0.1";
    static Integer SERVER_REGISTRY_PORT = 1099;
    static Integer CLIENT_REGISTRY_PORT = 1098;

    public static void main (String[] args) {

        // Parameter Arguments
        String server_registry_host = (args.length < 1) ? SERVER_REGISTRY_HOST : args[0];
        Integer server_registry_port = (args.length < 2) ? SERVER_REGISTRY_PORT : Integer.parseInt(args[1]);
        Integer client_registry_port = (args.length < 3) ? CLIENT_REGISTRY_PORT : Integer.parseInt(args[2]);

        try
        {
            // Init Server Object
            ServerImpl server = new ServerImpl();

            // Create Registry. TO-DO: remote creation
            Registry serverRegistry = LocateRegistry.createRegistry(server_registry_port);
            Registry clientRegistry = LocateRegistry.createRegistry(client_registry_port);

            // Stub and Skeleton
            Server stub = (Server) UnicastRemoteObject.exportObject(server,server_registry_port);



            serverRegistry.bind("Server", stub);

            System.out.println("[System] Game Server ready on " + InetAddress.getLocalHost().getHostAddress());

        }
        catch (Exception e)
        {
            System.err.println("[System] Error exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
