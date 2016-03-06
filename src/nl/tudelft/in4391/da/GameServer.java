package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GameServer {
    static String DEFAULT_REGISTRY_HOST = "127.0.0.1";
    static Integer DEFAULT_REGISTRY_PORT = 1099;
    static Integer DEFAULT_CALLBACK_PORT = 1100;

    public static void main (String[] args) {

        // Parameter Arguments
        String registry_host = (args.length < 1) ? DEFAULT_REGISTRY_HOST : args[0];
        Integer registry_port = (args.length < 2) ? DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]);

        try
        {
            // Init Server Object
            ServerImpl server = new ServerImpl();

            // Stub and Skeleton
            Server stub = (Server) UnicastRemoteObject.exportObject(server,registry_port);

            // TO-DO: remote creation
            Registry registry = LocateRegistry.createRegistry(registry_port);

            registry.bind("Server", stub);

            System.out.println("[System] Game Server ready on " + InetAddress.getLocalHost().getHostAddress());

        }
        catch (Exception e)
        {
            System.err.println("[System] Error exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
