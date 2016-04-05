package nl.tudelft.in4391.da;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public interface Server extends Remote {
    public boolean ping() throws RemoteException;
    public void register(Node node) throws RemoteException;
    public Player login(String username, String password) throws RemoteException;
    public void logout(Player player) throws RemoteException;

    // Arena
    public Arena getArena() throws RemoteException;
}
