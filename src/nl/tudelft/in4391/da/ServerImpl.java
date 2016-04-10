package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import java.io.Serializable;
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
    private ArrayList<Node> masterNodes = new ArrayList<Node>();
    private ArrayList<Node> workerNodes = new ArrayList<Node>();
    private NodeEvent nodeEvent;

    private ArrayList<Player> players = new ArrayList<Player>();
    private PlayerEvent playerEvent;
    private UnitEvent unitEvent;

    private Arena arena = new Arena();

    private long requestNum = 0;
    private long receiveNum = 0;

    private EventQueue eventQueue = new EventQueue();
    private EventQueue updateQueue = new EventQueue();

    private boolean dispatcher = false;


    public ServerImpl(Node node) {
        this.currentNode = node;

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
            public void onNewEvent(EventMessage em) {
                ArrayList<Unit> units = (ArrayList<Unit>) em.getObject();
                eventQueue.enqueue(em);
                System.out.println("[Client] Receive event from " + units.get(0).getFullName() + ". Queue: " + eventQueue.size());
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
                nodeEvent.send(NodeEvent.DISCONNECTED, node);

                System.out.println("Bye!");
            }
        });

        /*
         *  MAIN SERVER BLOCK
         *
         */

        // Initialize RMI Registry
        initRegistry();


        nodeEvent.listen();

        // Tell everyone this node is connected to cluster
        nodeEvent.send(NodeEvent.CONNECTED, node);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(masterNodes.size() > 1 && currentNode.getType().equals(Node.TYPE_MASTER) ) { // If this is the first master node alive
            syncData();
        }

        // If master.. you should dispatch a job
        if(currentNode.getType().equals(Node.TYPE_MASTER)) {
            playerEvent.listen();
            unitEvent.listen();

            dispatcher = true;
            new Thread ( new Runnable() {

                @Override
                public void run() {
                    while(dispatcher){
                        try {
                            Thread.sleep(1);
                            eventDispatcher();

                        } catch (InterruptedException e) {
                            //  e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    public void registerNode(Node node) {
        if (!nodes.contains(node)) {
            System.out.println("[System] "+node.getFullName()+" is connected.");
            this.nodes.add(node);

            if(node.getType().equals(Node.TYPE_MASTER)) this.masterNodes.add(node);
            else this.workerNodes.add(node);
        }
    }

    public void deregisterNode(Node node) {
        if (nodes.contains(node)) {
            System.out.println("[System] "+node.getFullName()+" is disconnected.");
            this.nodes.remove(node);

            if(node.getType().equals(Node.TYPE_MASTER)) this.masterNodes.remove(node);
            else this.workerNodes.remove(node);
        }
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
    public ArrayList<Node> getMasterNodes() {
        return masterNodes;
    }
    public ArrayList<Node> getWorkerNodes() {
        return workerNodes;
    }
    public synchronized void updateWorkerNode(Node node) {
        for (int i=0;i<workerNodes.size();i++) {
            if(workerNodes.get(i).equals(node)) {
                workerNodes.remove(i);
                workerNodes.add(node);
            }
        }
    }


    public void syncData() {
        System.out.println("[System] Sync data with active master node.");
        for(Node n: masterNodes){
            if(!n.equals(currentNode)) {
                Server remoteServer = fromRemoteNode(n);
                try {
                    this.players = remoteServer.getPlayers();
                    this.arena = remoteServer.getRemoteArena();
                    this.eventQueue = remoteServer.syncEventQueue();
                    break;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
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
        // We need to check the order to update

        this.arena = arena;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    // EVENT
    public EventQueue getEventQueue() { return eventQueue; }

    public EventQueue syncEventQueue() throws RemoteException{
        return this.eventQueue;
    }

    public EventQueue getUpdateQueue() { return updateQueue; }

    public synchronized void enqueue(EventMessage em) { eventQueue.enqueue(em); }
    public synchronized EventMessage dequeue() {
        EventMessage em = (EventMessage) eventQueue.dequeue();
        updateQueue.enqueue(em);
        return em;
    }

    public void eventDispatcher() {
        //System.out.println("[System] Dispatching job to workers.");
        if(!getEventQueue().isEmpty()) {
            EventMessage em = dequeue();
            if (em.getMaster().equals(currentNode)){
                //System.out.println("event available in queue");
                LinkedList<Node> bestNodes = new LinkedList<Node>();

                // Initialized two nodes
                bestNodes.addLast(getWorkerNodes().get(0));
                if(getWorkerNodes().size()>1) bestNodes.addLast(getWorkerNodes().get(1));

                // Find two best nodes based on its request num and status
                for (Node n : getWorkerNodes()) {
                    if((n.getRequestNum() < bestNodes.getFirst().getRequestNum()) && (n.getStatus()==Node.STATUS_READY)) {
                        bestNodes.remove();
                        bestNodes.addLast(n);
                    }
                }

                // Dispatch the job to 2 (or 1) best nodes
                Server worker = null;

                // increase current node request number clock
                increaseRequestNum();

                // assign this node request number
                em.setRequestNum(getRequestNum());

                //System.out.println(em.getCode() );

                for (Node n : bestNodes) {
                    // Set target node as Busy
                    n.setType(Node.STATUS_BUSY);
                    updateWorkerNode(n);

                    // Dispatch
                    worker = fromRemoteNode(n);
                    try {
                        System.out.println("[System] Dispatch event "+em.getId()+" to Worker "+n.getFullName()+" [Queue: "+eventQueue.size()+"]");
                        worker.executeEvent(currentNode, getArena(), em);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        System.out.println(em.getId());
                        e.printStackTrace();
                    }
                }
            }

        } else {
//            System.out.println("no job available");
        }
    }

    public void increaseRequestNum() {
        this.requestNum++;
    }

    public long getRequestNum(){
        return this.requestNum;
    }

    public void increaseReceiveNum() {
        this.receiveNum++;
    }

    public long getReceiveNum(){
        return this.receiveNum;
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

    public Arena getArena() { return arena; }

    public Arena getRemoteArena() { return this.arena; }

    // EventQueue to Worker
    @Override
    public void sendEvent(Integer code, ArrayList<Unit> units) throws RemoteException {
        System.out.println("[System] Receive new client unit movement for " + units.get(0).getFullName() + " from " + units.get(0).getCoord() + " to " + units.get(1).getCoord());
        EventMessage em = new EventMessage(code, units);
        em.setMaster(currentNode);
        unitEvent.send(em);
    }

    @Override
    public void executeEvent(Node n, Arena a, EventMessage em) throws RemoteException {
        System.out.println("[System] Receive new event "+em.getId()+" job from Master "+n.getFullName()+".");

        currentNode.increaseRequestNum();
        currentNode.setType(Node.STATUS_BUSY);

        ArrayList<Unit> units = (ArrayList<Unit>) em.getObject();

        System.out.println("[System] Calculate moving unit "+units.get(0).getFullName()+" from "+units.get(0).getCoord()+" to "+units.get(1).getCoord());

        if(em.getCode() == UnitEvent.UNIT_MOVE) {
            a.moveUnit(units.get(0),units.get(1).getX(),units.get(1).getY());
        } else if(em.getCode() == UnitEvent.UNIT_ATTACK) {
            a.attackUnit(units.get(0),units.get(1));
        } else if(em.getCode() == UnitEvent.UNIT_HEAL) {
            a.healUnit(units.get(0),units.get(1));
        }

        currentNode.setType(Node.STATUS_READY);

        Server server = ServerImpl.fromRemoteNode(n);
        server.processedEvent(currentNode, a, em);

        System.out.println("[System] Completed job from Master "+n.getFullName()+".");
    }

    public synchronized void setArena(Arena a) {
        this.arena = a;
    }
    @Override
    public void processedEvent(Node node, Arena a, EventMessage em ) throws RemoteException {
        System.out.println("[System] Receive processed event job from Worker "+ node.getFullName()+".");

        if(em.getRequestNum()>getReceiveNum()) {
            boolean is_head = em.equals(updateQueue.peek());

            if (is_head) {
                System.out.println("[System] Updating Arena provided provided by " + node.getFullName());
                increaseReceiveNum();
                updateQueue.dequeue();
                setArena(a);

                Server s = null;
                for(Node n: masterNodes) {
                    if(!n.equals(currentNode)||!n.equals(node)) { // Not current node or the one who send it
                        try {
                            s.processedEvent(currentNode, a, em);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                System.out.println("[System] Waiting for another Worker to complete.");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean retry = true;
                        boolean is_head = false;
                        while (retry) { // this EventMessage is already in the head, update
                            is_head = em.equals(updateQueue.peek());
                            if (is_head) {
                                increaseReceiveNum();
                                updateQueue.dequeue();
                                setArena(a);

                                //System.out.println("Send sync to other master nodes");
                                Server s = null;
                                for(Node n: masterNodes) {
                                    if(!n.equals(currentNode)||!n.equals(node)) { // Not current node or the one who send it
                                        try {
                                            s.processedEvent(currentNode, a, em);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                retry = false;
                            }
                        }
                    }
                }).start();
            }
        }
    }
}
