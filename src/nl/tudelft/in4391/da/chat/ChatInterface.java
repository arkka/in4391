package nl.tudelft.in4391.da.chat;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.rmi.*;

public interface ChatInterface extends Remote{
    public String getName() throws RemoteException;
    public void send(String msg) throws RemoteException;
    public void setClient(ChatInterface c)throws RemoteException;
    public ChatInterface getClient() throws RemoteException;
    public String debug() throws RemoteException;
}
