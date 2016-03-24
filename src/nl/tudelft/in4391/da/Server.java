package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public interface Server extends Remote {
    public void register(Node node) throws RemoteException;
    public Boolean connect() throws RemoteException;
    public Player login(String username, String password) throws RemoteException;

    // Sync
    public Arena getArena(Node node) throws RemoteException;
    public ArrayList<Player> getPlayers(Node node) throws RemoteException;


    public Unit spawnUnit(Unit unit) throws RemoteException;

    public Unit moveUnit(Unit unit, int x, int y) throws RemoteException;

    public Unit removeUnit(Unit unit, int x, int y) throws RemoteException;

    public void deleteUnit(Unit unit) throws RemoteException;

    public boolean checkSurrounding(Unit unit, int x, int y) throws RemoteException;

    public Unit getSurroundingUnit(int x, int y) throws RemoteException;

    public void logout(Player player) throws RemoteException;
}
