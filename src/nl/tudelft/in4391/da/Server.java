package nl.tudelft.in4391.da;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Server extends Remote {
    public void login(Client client) throws RemoteException;
    public void logout() throws RemoteException;
    public Boolean ping() throws RemoteException;

    public Client getClient() throws RemoteException;
    public void setClient(Client client) throws RemoteException;
}
