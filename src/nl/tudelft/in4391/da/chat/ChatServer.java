package nl.tudelft.in4391.da.chat;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import nl.tudelft.in4391.da.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatServer {
    public static void main (String[] argv) {
        try
        {
            // Username Input
            Scanner s=new Scanner(System.in);
            System.out.println("Username: ");
            String username=s.nextLine().trim();

            // Init Chat Object
            Chat obj = new Chat(new User(username));

            ChatInterface stub = (ChatInterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Chat", stub);

            System.out.println("[System] Chat Remote Object is ready.");

        }
        catch (Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        /*
        try {
            System.setSecurityManager(new RMISecurityManager());
            Scanner s=new Scanner(System.in);
            System.out.println("Enter Your name and press Enter:");
            String name=s.nextLine().trim();

            Chat server = new Chat(name);

            Naming.rebind("rmi://localhost/ABC", server);

            System.out.println("[System] Chat Remote Object is ready:");

            while(true){
                String msg=s.nextLine().trim();
                if (server.getClient()!=null){
                    ChatInterface client=server.getClient();
                    msg="["+server.getName()+"] "+msg;
                    client.send(msg);
                }
            }

        }catch (Exception e) {
            System.out.println("[System] Server failed: " + e);
        }
        */
    }
}
