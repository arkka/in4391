package nl.tudelft.in4391.da;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ClientImpl implements Client {
    private Server server;
    private String username;
    public InetAddress address;

    public ClientImpl(String username) {
        this.username = username;

        try {
            this.address = InetAddress.getLocalHost();
        } catch(UnknownHostException e){
            System.err.println("[Error] Unable to retrieve local address. Exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void connected(Server server) throws RemoteException {
        setSever(server);
        //System.out.println("[System] Successfully connected to server "+getServer().getAddress().getHostAddress());
        System.out.println("[System] Successfully connected to server as "+getUsername()+".");
    }

    @Override
    public void pong() throws RemoteException {
        //System.out.println("[System] Reply pong from server "+getServer().getAddress().getHostAddress());
        System.out.println("[System] Reply pong from server");
    }

    @Override
    public Server getServer() throws RemoteException {
        return this.server;
    }

    @Override
    public void setSever(Server server) throws RemoteException {
        this.server = server;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) throws RemoteException {
        this.username = username;
    }

}
