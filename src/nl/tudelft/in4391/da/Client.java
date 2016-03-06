package nl.tudelft.in4391.da;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Client extends Remote {
    String getArena() throws RemoteException;
}
