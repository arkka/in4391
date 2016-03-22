package nl.tudelft.in4391.da;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Server extends Remote {
    public void register(Node node) throws RemoteException;
    public Boolean connect() throws RemoteException;
    public Player login(String username, String password) throws RemoteException;

    public void logout(Player player) throws RemoteException;
}
