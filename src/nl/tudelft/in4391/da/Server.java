package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public interface Server extends Remote {
    public boolean ping() throws RemoteException;
    public void register(Node node) throws RemoteException;
    public Player login(String username, String password, String type) throws RemoteException;
    public void logout(Player player) throws RemoteException;

    public ArrayList<Player> getPlayers() throws RemoteException;

    public EventQueue syncEventQueue() throws RemoteException;

    // Arena
    public Arena getArena() throws RemoteException;

    // Events
    public void sendEvent(Integer code, ArrayList<Unit> units) throws RemoteException;
    public void executeEvent(Node node, Arena a, EventMessage em) throws RemoteException;
    public void processedEvent(Node node, Arena a, EventMessage em) throws RemoteException;

}
