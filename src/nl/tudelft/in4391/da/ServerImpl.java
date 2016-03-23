package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class ServerImpl implements Server {
    private Node node;
    private ArrayList<Node> activeNodes;
    private ArrayList<Node> masterNodes;
    private ArrayList<Node> workerNodes;

    private ArrayList<Player> activePlayers;
    private Event event;

    private Arena arena;

    public ServerImpl(Node node) {
        this.node = node;

        // Init Array List
        this.activeNodes = new ArrayList<Node>();
        this.masterNodes = new ArrayList<Node>();
        this.workerNodes = new ArrayList<Node>();

        this.activePlayers = new ArrayList<Player>();

        // Init Arena
        this.arena = new Arena();

        // Add current node
        addActiveNode(node);

        // Init RMI Registry
        initRegistry();

        // Init Event Thread
        initEventThread(GameServer.DEFAULT_MULTICAST_GROUP,GameServer.DEFAULT_SOCKET_PORT);

        // Send Node Active State
        event.send(Event.NODE_CONNECTED,node);
    }

    // GETTERS SETTERS
    public Node getNode() { return this.node; }

    // Registry
    public void initRegistry(){
        // Create New Registry
        try {
            LocateRegistry.createRegistry(this.node.getRegistryPort());
        } catch (RemoteException e) {
            System.err.println("[Error] Exception: " + e.toString());
            e.printStackTrace();
        }

        // Get Registry
        Registry registry = null;
        Server stub = null;
        try {

            // Get Local Registry
            registry = LocateRegistry.getRegistry(this.node.getRegistryPort());

            // Stub and Skeleton
            stub = (Server) UnicastRemoteObject.exportObject(this,this.node.getCallbackPort());
            registry.bind(node.getName(), stub);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NODES
    public ArrayList<Node> getActiveNodes() {
        return this.activeNodes;
    }
    public ArrayList<Node> getMasterNodes() {
        return this.masterNodes;
    }
    public ArrayList<Node> getWorkerNodes() {
        return this.workerNodes;
    }

    public void addActiveNode(Node node) {
        if(!activeNodes.contains(node)) {
            this.activeNodes.add(node);

            // Master
            if(node.getID()<=2) {
                this.masterNodes.add(node);
                this.masterNodes = sortNodes(this.masterNodes);
            }
            else { // Worker
                this.workerNodes.add(node);
                this.workerNodes = sortNodes(this.workerNodes);
            }

            this.activeNodes = sortNodes(this.activeNodes);
        }
    }

    public void removeActiveNode(Node node) {
        if(activeNodes.contains(node)) {
            this.getActiveNodes().remove(activeNodes.indexOf(node));

            if(node.getID()<= 2 && this.masterNodes.contains(node)) {
                this.getMasterNodes().remove(masterNodes.indexOf(node));
                this.masterNodes = sortNodes(this.masterNodes);
            } if(node.getID()> 2 && this.workerNodes.contains(node)) {
                this.getWorkerNodes().remove(workerNodes.indexOf(node));
                this.workerNodes = sortNodes(this.workerNodes);
            }

            this.activeNodes = sortNodes(this.activeNodes);
        }
    }

    public ArrayList<Node> sortNodes(ArrayList<Node> nodes) {
        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node node2, Node node1)
            {
                return  node2.getID().compareTo(node1.getID());
            }
        });

        return nodes;
    }

    // PLAYERS

    public ArrayList<Player> getActivePlayers() {
        return this.activePlayers;
    }

    public void addActivePlayer(Player player) {
        if(!activePlayers.contains(player)) {
            this.activePlayers.add(player);
        }
    }

    public void removeActivePlayer(Player player) {
        if(activePlayers.contains(player)) {
            this.getActivePlayers().remove(activePlayers.indexOf(player));
        }
    }

    // THREAD
    public void shutdown() {
        this.event.send(101,getNode());
    }

    // STATIC method
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

    private void initEventThread(String group, int port) {
        try {
            this.event = new Event(group,port);
            this.event.setListener(new EventListener() {
                @Override
                public void onReceiveData(byte[] receiveData, int length) {
                    // RAW Event Message
                    try {
                        EventMessage message = EventMessage.fromByte(receiveData);
                        if(message.getCode()==Event.NODE_CONNECTED) {
                            onNodeConnected((Node) message.getObject());
                        } else if(message.getCode()==Event.NODE_DISCONNECTED) {
                            onNodeDisconnected((Node) message.getObject());
                        } else if(message.getCode()==Event.PLAYER_CONNECTED) {
                            onPlayerConnected((Player) message.getObject());
                        } else if(message.getCode()==Event.PLAYER_DISCONNECTED) {
                            onPlayerDisconnected((Player) message.getObject());
                        } else if(message.getCode()==Event.UNIT_SPAWN) {
                            onUnitSpawned((Unit) message.getObject());
                        } else if(message.getCode()==Event.UNIT_MOVED) {
                            onUnitMoved((Unit) message.getObject());
                        }
                        else if(message.getCode()==Event.UNIT_REMOVED) {
                            onUnitRemoved((Unit) message.getObject());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            this.event.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // EVENTS
    public void onNodeConnected(Node n){
        addActiveNode(n);
        System.out.println("[System] " + n.getFullName() + " is connected.");

        if(!this.node.getID().equals(n.getID())) { // if the multicast not from himself
            Server remoteComponent = ServerImpl.fromRemoteNode(n);
            try {
                remoteComponent.register(this.node);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onNodeDisconnected(Node n){
        removeActiveNode(n);
        System.out.println("[System] " + n.getFullName() + " is disconnected.");

    }

    public void onPlayerConnected(Player p) {
        addActivePlayer(p);
        System.out.println("[System] " + p.getUsername() + " is logged in.");

    }

    public void onPlayerDisconnected(Player p) {
        removeActivePlayer(p);
        System.out.println("[System] " + p.getUsername() + " is logged out.");
    }

    private void onUnitSpawned(Unit u) {
        System.out.println("[System] "+u.getName()+" spawned at coord (" + u.getX() + "," + u.getY() + ") of the arena.");
    }

    private void onUnitMoved(Unit u) {
        System.out.println("[System] "+u.getName()+" moved to coord (" + u.getX() + "," + u.getY() + ") of the arena.");
    }

    private void onUnitRemoved(Unit u) {
        System.out.println("[System] "+u.getName()+" moved from coord (" + u.getX() + "," + u.getY() + ") of the arena.");
    }

    // REMOTE FUNCTIONS
    @Override
    public void register(Node remoteNode) {
        addActiveNode(remoteNode);
        System.out.println("[System] " + remoteNode.getFullName() + " is connected.");
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
                event.send(Event.PLAYER_CONNECTED,player);
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
    public Arena getArena(Node node) throws RemoteException {
        return this.arena;
    }

    @Override
    public Unit spawnUnit(Unit unit) throws RemoteException {
        unit = arena.spawnUnitRandom(unit);
        event.send(Event.UNIT_SPAWN,unit);
        return unit;
    }

    @Override
    public Unit moveUnit(Unit unit, int x, int y) throws RemoteException {
        unit = arena.moveUnit(unit, x, y);
        event.send(Event.UNIT_MOVED, unit);
        return unit;
    }

    @Override
    public Unit removeUnit(Unit unit, int x, int y) throws RemoteException {
        arena.removeUnit(unit, x, y);
        event.send(Event.UNIT_REMOVED, unit);
        return unit;
    }

    @Override
    public void logout(Player player) throws RemoteException {
        //player.setAuthenticated(false);
        //System.out.println("[System] Player '" + player + "' has logout.");
    }

}
