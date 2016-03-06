package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class GameServer {
    static String SERVER_REGISTRY_HOST = "127.0.0.1";
    static Integer SERVER_REGISTRY_PORT = 1099;
    static Integer SERVER_CALLBACK_PORT = 1100;
    static Integer CLIENT_REGISTRY_PORT = 1098;

    public static void main (String[] args) {

        // Parameter Arguments
        String server_registry_host = (args.length < 1) ? SERVER_REGISTRY_HOST : args[0];
        Integer server_callback_port = (args.length < 2) ? SERVER_CALLBACK_PORT : Integer.parseInt(args[1]);

        // Init Server Object
        ServerImpl server = new ServerImpl();

        try
        {
            LocateRegistry.createRegistry(SERVER_REGISTRY_PORT);
            LocateRegistry.createRegistry(CLIENT_REGISTRY_PORT);
        }
        catch (ExportException e)
        {
            System.out.println("[Warning] Registry is already exist.");
        }
        catch (Exception e)
        {
            System.err.println("[System] Error Exception: " + e.toString());
            e.printStackTrace();
        }

        Registry serverRegistry = null;
        try {
            serverRegistry = LocateRegistry.getRegistry(server_registry_host,SERVER_REGISTRY_PORT);
            // Stub and Skeleton
            Server stub = (Server) UnicastRemoteObject.exportObject(server,server_callback_port);

            serverRegistry.bind("Server-" + UUID.randomUUID(), stub);

            System.out.println("[System] Game Server ready on " + InetAddress.getLocalHost().getHostAddress());

            System.out.println("\n[Debug: Server Registry]");
            for(String sr : serverRegistry.list()) {
                System.out.println("- "+sr);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
