package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GameServer {
    static Integer DEFAULT_PORT = 1101; // 1101 ~ 1200

    public static void main (String[] args) {

        // Parameter Arguments
        Integer port = (args.length < 1) ? DEFAULT_PORT : Integer.parseInt(args[1]);

        try
        {
            // Init Server Object
            ServerImpl server = new ServerImpl();

            // Stub and Skeleton
            Server stub = (Server) UnicastRemoteObject.exportObject(server,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Server", stub);

            System.out.println("[System] Server is ready on " + InetAddress.getLocalHost().getHostAddress() + ":" + port);

        }
        catch (Exception e)
        {
            System.err.println("[System] Error exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
