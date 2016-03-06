package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ServerImpl implements Server {
    private Client client;

    public ServerImpl() {

    }

    @Override
    public void ping() throws RemoteException {
        System.out.println("[System] Received ping from ...");

        // Reply client's ping
        client.pong();
    }

    @Override
    public Client getClient() throws RemoteException {
        return this.client;
    }

    @Override
    public void setClient(Client client) throws RemoteException {
        this.client = client;
    }

    @Override
    public void connect(Client client) throws RemoteException {
        setClient(client);
        //System.out.println("[System] A new client has connected from "+getClient().getAddress().getHostAddress());
        System.out.println("[System] A new client '"+client.getUsername()+"' has connected.");

        // ACK success connection to client
        client.connected(this);
    }

    @Override
    public void disconnect() throws RemoteException {

    }

}
