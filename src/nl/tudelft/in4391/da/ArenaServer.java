package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ArenaServer {
    public static void main (String[] args) {

        // Parameter Arguments
        String registry_host = (args.length < 1) ? GameServer.DEFAULT_REGISTRY_HOST : args[0];
        Integer registry_port = (args.length < 2) ? GameServer.DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]); // 1100 ~ ...
        Integer callback_port = (args.length < 3) ? GameServer.DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]); // 1500 ~ ...

        try
        {
            // ...

        }
        catch (Exception e)
        {
            System.err.println("[System] Error exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
