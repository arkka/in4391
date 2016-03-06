package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Client extends Remote {
    public void connected(Server server) throws RemoteException;
    public void pong() throws RemoteException;

    public Server getServer() throws RemoteException;
    public void setSever(Server server) throws RemoteException;

    public String getUsername() throws RemoteException;

}
