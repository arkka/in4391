package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Server extends Remote {
    public void connect(Client client) throws RemoteException;
    public void disconnect() throws RemoteException;
    public void ping() throws RemoteException;

    public Client getClient() throws RemoteException;
    public void setClient(Client client) throws RemoteException;
}
