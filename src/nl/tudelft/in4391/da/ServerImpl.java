package nl.tudelft.in4391.da;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ServerImpl implements Server {
    private Node node;
    private ArrayList<Node> activeNodes;
    private ArrayList<Player> activePlayers;

    public ServerImpl(Node node) {
        this.node = node;
        this.activeNodes = new ArrayList<Node>();
        this.activePlayers = new ArrayList<Player>();
        addActiveNode(node);
    }

    public static Server fromRemoteNode(Node node) {
        Server component = null;
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(node.getHostAddress(), node.getRegistryPort());
            component = (Server) remoteRegistry.lookup(node.getName());
            return component;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return component;
    }

    public ArrayList<Node> getActiveNodes() {
        return this.activeNodes;
    }

    public void addActiveNode(Node node) {
        if(!activeNodes.contains(node)) {
            this.activeNodes.add(node);
            sortActiveNodes();
        }
    }

    public void sortActiveNodes() {
        Collections.sort(this.activeNodes, new Comparator<Node>() {
            @Override
            public int compare(Node node2, Node node1)
            {
                return  node2.getID().compareTo(node1.getID());
            }
        });
    }

    public ArrayList<Player> getActivePlayers() {
        return this.activePlayers;
    }

    public void addActivePlayer(Player player) {
        if(!activePlayers.contains(player)) {
            this.activePlayers.add(player);
        }
    }


    // REMOTE FUNCTIONS
    @Override
    public void register(Node remoteNode) {
        addActiveNode(remoteNode);
        System.out.println("[System] " + remoteNode.getFullName() + " is active.");
    }

    @Override
    public Boolean connect() {
        return true;
    }

    @Override
    public Player login(String username, String password) throws RemoteException {
        // Init player object
        Player player = new Player(username, password);

        // TO-DO Check credentials
        if(true) {
            player.setAuthenticated(true);
            try {
                player.setHostAddress(RemoteServer.getClientHost());
                addActivePlayer(player);
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
            System.out.println("[System] Player " + player + " has logged in.");
        } else {
            System.out.println("[Error] Bad credentials.");
        }
        return player;
    }

    @Override
    public void logout(Player player) throws RemoteException {
        //player.setAuthenticated(false);
        //System.out.println("[System] Player '" + player + "' has logout.");
    }

}
