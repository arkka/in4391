package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Server extends Remote {
    public void register(Node node) throws RemoteException;
    public Boolean connect() throws RemoteException;
    public Player login(String username, String password) throws RemoteException;

    public Unit spawnUnit(Player player) throws RemoteException;

    public void logout(Player player) throws RemoteException;
}
