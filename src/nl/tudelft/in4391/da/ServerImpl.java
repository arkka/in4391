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
    public Boolean ping() throws RemoteException {
        try {
            System.out.println("[System] Incoming client connection from "+RemoteServer.getClientHost());
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return true;
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
    public void login(Client client) throws RemoteException {
        // TO-DO: Credentials Authentication
        setClient(client);

        try {
            // Init new player object
            Player player = new Player(client.getUsername(),RemoteServer.getClientHost());
            System.out.println("[System] Player "+player.toString()+" has logged in.");

            // ACK authentication
            client.authenticated(this, player);
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void logout() throws RemoteException {
        System.out.println("[System] Player '"+client.getUsername()+"' has logout.");
        setClient(null);

    }

}
