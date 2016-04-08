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
public class WorkerImpl implements Server {
    private Node currentNode;
    private ArrayList<Node> nodes = new ArrayList<Node>();
    private NodeEvent nodeEvent;

    private ArrayList<Player> players = new ArrayList<Player>();
    private PlayerEvent playerEvent;
    private UnitEvent unitEvent;

    private Arena arena = new Arena();

    private EventQueue eventQueue = new EventQueue();


    public WorkerImpl(Node node) {
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
    public EventQueue getEventQueue() { return eventQueue; }

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

    }

    @Override
    public Player login(String username, String password, String type) throws RemoteException {
        return null;
    }

    @Override
    public void logout(Player p) throws RemoteException {

    }

    @Override
    public Arena getArena() throws RemoteException {
        return arena;
    }

    @Override
    public void setArena(Arena arena) throws RemoteException {

    }

    @Override
    public EventMessage fetchEvent() throws RemoteException {
        return null;
    }

    // EventQueue to Worker

    @Override
    public void moveUnit(Unit u, int x, int y) throws RemoteException {
        // not aplicable for worker
    }

    @Override
    public void attackUnit(Unit source, Unit target) throws RemoteException {
        // not aplicable for worker

    }

    @Override
    public void healUnit(Unit source, Unit target) throws RemoteException {
        // not aplicable for worker
    }

}
