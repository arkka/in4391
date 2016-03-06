package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ServerImpl implements Server {
    private Client client;

    @Override
    public Boolean connect() throws RemoteException {
        try {
            System.out.println("[System] Established client connection from "+RemoteServer.getClientHost());
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public Player login(String username, String password) throws RemoteException {
        // Init player object
        Player player = new Player(username, password);

        // TO-DO Check credentials
        if(true) {
            player.setAuthenticated(true);
            try {
                player.setHostAddress(RemoteServer.getClientHost());
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
            System.out.println("[System] Player " + player + " has logged in.");
        } else {
            System.out.println("[Error] Bad credentials.");
        }

        return player;
    }

    @Override
    public void logout() throws RemoteException {
        client.getPlayer().setAuthenticated(false);
        System.out.println("[System] Player '" + client.getPlayer() + "' has logout.");
    }

}
