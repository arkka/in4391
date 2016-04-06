package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static java.rmi.server.RemoteServer.getClientHost;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class ServerImpl implements Server {
    private Node currentNode;
    private ArrayList<Node> nodes = new ArrayList<Node>();
    private NodeEvent nodeEvent;

    private ArrayList<Player> players = new ArrayList<Player>();
    private PlayerEvent playerEvent;

    private Arena arena = new Arena();

    public ServerImpl(Node node) {
        this.currentNode = node;

        // Initialize RMI Registry
        initRegistry();

        /*
         *  EVENT: NODE
         */
        nodeEvent = new NodeEvent(node) {
            @Override
            public void onConnected(Node n) {
                registerNode(n);

                if(!node.equals(n)) {
                    Server remoteServer = fromRemoteNode(n);
                    try {
                        remoteServer.register(node);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onDisconnected(Node n) {
                deregisterNode(n);

            }
        };

        // Tell everyone this node is connected to cluster
        nodeEvent.send(NodeEvent.CONNECTED,node);


        /*
         *  EVENT: PLAYER
         */
        playerEvent = new PlayerEvent(node) {
            @Override
            public void onLoggedIn(Player p) {
                // Do I need to register new connected user here? or put it all on sync?

                if(!node.equals(p)) {
                    registerPlayer(p);
                }
            }

            @Override
            public void onLoggedOut(Player p) {
                deregisterPlayer(p);
            }
        };

        /*
         *  SHUTDOWN THREAD
         *  Exit gracefully
         */

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Node terminating...");

                // Tell everyone this node is disconnected from cluster
                nodeEvent.send(NodeEvent.DISCONNECTED,node);

                System.out.println("Bye!");
            }
        });
    }

    public void registerNode(Node node) {
        if (!nodes.contains(node)) {
            System.out.println("[System] "+node.getFullName()+" is connected.");
            this.nodes.add(node);
        }
    }

    public void deregisterNode(Node node) {
        if (nodes.contains(node)) {
            System.out.println("[System] "+node.getFullName()+" is disconnected.");
            this.nodes.remove(node);
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void registerPlayer(Player player) {
        if (!players.contains(player)) {
            System.out.println("[System] "+player.getUsername()+" is logged in.");
            this.players.add(player);
        }
    }

    public void deregisterPlayer(Player player) {
        if (players.contains(player)) {
            System.out.println("[System] "+player.getUsername()+" is logged out.");
            this.players.remove(player);
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }



    public void initRegistry(){
        System.out.println("[System] Initialize remote registry.");
        try {
            LocateRegistry.createRegistry(currentNode.getRegistryPort());
            System.out.println("[System] Remote registry initialized.");
        } catch (RemoteException e) {
            System.err.println("[Error] Exception: " + e.toString());
            e.printStackTrace();
        }

        System.out.println("[System] Register server to the remote registry");
        Registry registry = null;
        Server stub = null;
        try {
            registry = LocateRegistry.getRegistry(currentNode.getRegistryPort());
            stub = (Server) UnicastRemoteObject.exportObject(this,currentNode.getCallbackPort());
            registry.bind(currentNode.getName(), stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Static method
    public static Server fromRemoteNode(Node node) {
        Server remoteServer = null;
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(node.getHostAddress(), node.getRegistryPort());
            remoteServer = (Server) remoteRegistry.lookup(node.getName());
            return remoteServer;
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
        return remoteServer;
    }

    // Remote
    @Override
    public boolean ping() throws RemoteException {
        try {
            System.out.println("[System] Incoming ping from "+getClientHost());
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void register(Node node) throws RemoteException {
        registerNode(node);
    }

    @Override
    public Player login(String username, String password, String type) throws RemoteException {
        Player player = new Player(username, password);
        if(true) {
            try {
                player.setAuthenticated(true);
                player.setHostAddress(RemoteServer.getClientHost());
                System.out.println("[System] Player " + player + " has logged in.");

                // TO-DO SEND to WORKER via EventQueue
//                Knight knight = new Knight(player.getUsername());
                Unit unit = null;
                if(type.equals("Dragon")) {
                    unit = new Dragon(player.getUsername());
                } else unit = new Knight(player.getUsername());

                arena.spawnUnit(unit);

                player.setUnit(unit);
                System.out.println("[System] "+unit.getFullName()+" spawned at coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");

                // Notify others
                playerEvent.send(playerEvent.LOGGED_IN,player);

            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("[Error] Bad credentials.");
        }
        return player;
    }

    @Override
    public void logout(Player p) throws RemoteException {
        arena.removeUnit(p.getUnit());
        deregisterPlayer(p);
        System.out.println("[System] Player `" + p + "` has logged out.");
    }

    @Override
    public Arena getArena() throws RemoteException {
        return arena;
    }

    public void releaseDragons(int num){
        System.out.println("[System] Releasing dragons to the arena.");
        // Release dragons to arena
        int idragon = 1;

        while (arena.getDragons().isEmpty() || arena.getDragons().size() < num) {
            Dragon dragon = new Dragon("Dragon-"+idragon);
            arena.spawnUnit(dragon);
            idragon++;
            System.out.println("[System] " + dragon.getName() + " is active with " + dragon.getHitPoints() + " HP and " + dragon.getAttackPoints() + " AP.");
        }
    }

    @Override
    public void moveUnit(Unit u, int x, int y) throws RemoteException {
        arena.moveUnit(u, x, y);
    }
}
