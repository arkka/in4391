package nl.tudelft.in4391.da;

import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ClientImpl implements Client {
    private Server server;
    private Player player;

    public ClientImpl() {
        //this.player = new Player(username,password);
    }

    @Override
    public Player getPlayer() throws RemoteException {
        return this.player;
    }
}
