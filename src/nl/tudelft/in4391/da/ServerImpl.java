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
import java.util.LinkedList;

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
    private UnitEvent unitEvent;

    private Arena arena = new Arena();

    private EventQueue eventQueue = new EventQueue();


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
                arena.removeUnit(p.getUnit());
                deregisterPlayer(p);
                System.out.println("[System] Player `" + p + "` has logged out.");
            }

        };


        /*
         *  EVENT: UNIT
         */
        unitEvent = new UnitEvent(node) {
            @Override
            public void onMove(Unit s, Unit t) {
                //System.out.println("[System] " + u.getFullName() + " move to (" + u.getX() + "," + u.getY() + ")");

                ArrayList<Unit> units = new ArrayList<Unit>();
                units.add(s);
                units.add(t);

                EventMessage em = new EventMessage(unitEvent.UNIT_MOVE, units);
                eventQueue.enqueue(em);
            }

            @Override
            public void onAttack(Unit s, Unit t) {
                /*
                Integer lastHp = t.getHitPoints();
                if (t.getHitPoints() <=0){
                    lastHp = 0;
                }
                */

                //System.out.println("[System] " + s.getFullName() + " attack " + t.getFullName() + " to " + lastHp + "/" + t.getMaxHitPoints());

                ArrayList<Unit> units = new ArrayList<Unit>();
                units.add(s);
                units.add(t);

                EventMessage em = new EventMessage(unitEvent.UNIT_ATTACK, units);
                eventQueue.enqueue(em);

            }

            @Override
            public void onHeal(Unit s, Unit t) {
                //System.out.println("[System] " + s.getFullName() + " heal " + t.getFullName() + " to " + t.getHitPoints() + "/" + t.getMaxHitPoints());

                ArrayList<Unit> units = new ArrayList<Unit>();
                units.add(s);
                units.add(t);

                EventMessage em = new EventMessage(unitEvent.UNIT_HEAL, units);
                eventQueue.enqueue(em);

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

    public synchronized void syncArena(Arena arena) {
        this.arena = arena;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public EventQueue getEventQueue() { return eventQueue; }
    public synchronized void enqueue(EventMessage em) { eventQueue.enqueue(em); }
    public synchronized EventMessage dequeue() { return (EventMessage) eventQueue.dequeue(); }

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

                Unit unit = null;
                if(type.equals("Dragon")) {
                    unit = new Dragon(player.getUsername());
                } else unit = new Knight(player.getUsername());

                arena.spawnUnit(unit);
                player.setUnit(unit);
                System.out.println("[System] "+unit.getFullName()+" spawned at coord (" + unit.getX() + "," + unit.getY() + ") of the arena.");

                // Notify other masters
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
        // Notify other masters
        playerEvent.send(playerEvent.LOGGED_OUT,p);
    }

    @Override
    public Arena getArena() throws RemoteException {
        return arena;
    }

    @Override
    public void setArena(Arena arena) throws RemoteException {
        syncArena(arena);
    }

    // EventQueue to Worker

    @Override
    public void moveUnit(Unit s, Unit t) throws RemoteException {
        // Notify other masters
        //arena.moveUnit(u, x, y);

        ArrayList<Unit> units = new ArrayList<Unit>();
        units.add(s);
        units.add(t);

        unitEvent.send(unitEvent.UNIT_MOVE, units);

        //EventMessage em = new EventMessage(unitEvent.UNIT_MOVE, u);
        //eventQueue.enqueue(em);
    }

    @Override
    public void attackUnit(Unit source, Unit target) throws RemoteException {
        //arena.attackUnit(source, target);

        ArrayList<Unit> units = new ArrayList<Unit>();
        units.add(source);
        units.add(target);

        //// Notify others
        unitEvent.send(unitEvent.UNIT_ATTACK, units);

        //EventMessage em = new EventMessage(unitEvent.UNIT_ATTACK, units);
        //eventQueue.enqueue(em);

    }

    @Override
    public void healUnit(Unit source, Unit target) throws RemoteException {
        //arena.healUnit(source, target);

        ArrayList<Unit> units = new ArrayList<Unit>();
        units.add(source);
        units.add(target);

        // Notify others
        unitEvent.send(unitEvent.UNIT_HEAL, units);

        //EventMessage em = new EventMessage(unitEvent.UNIT_HEAL, units);
        //eventQueue.enqueue(em);
    }

}
