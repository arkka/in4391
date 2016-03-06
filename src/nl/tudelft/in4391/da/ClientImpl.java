package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ClientImpl implements Client {
    private Server server;
    private Player player;

    private String username;
    private String password; // unimplemented

    public ClientImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void authenticated(Server server, Player player) throws RemoteException {
        setServer(server);
        setPlayer(player);
        System.out.println("[System] Successfully authenticated to server as "+player.toString()+".");
    }

    @Override
    public Server getServer() throws RemoteException {
        return this.server;
    }

    @Override
    public void setServer(Server server) throws RemoteException {

    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public Server getPlayer() throws RemoteException {
        return this.server;
    }

    public void setPlayer(Player player) throws RemoteException {
        this.player = player;
    }
}
