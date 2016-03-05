package nl.tudelft.in4391.da.chat;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatClient {
    private ChatClient() {};

    public static void main(String[] args)
    {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ChatInterface stub = (ChatInterface) registry.lookup("Chat");

            System.out.println("Hello, " + stub.getClient().getUser());
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
