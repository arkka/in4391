package nl.tudelft.in4391.da;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ServerImpl implements Server {
    Player player;

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
    public Boolean login(String username, String password) throws RemoteException {
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

            this.player = player;
            System.out.println("[System] Player " + player + " has logged in.");
            return true;

        } else {
            System.out.println("[Error] Bad credentials.");
        }

        return false;
    }

    @Override
    public Player getPlayer() throws RemoteException {
        return this.player;
    }

    @Override
    public void logout(Player player) throws RemoteException {
        player.setAuthenticated(false);
        System.out.println("[System] Player '" + player + "' has logout.");
    }

}