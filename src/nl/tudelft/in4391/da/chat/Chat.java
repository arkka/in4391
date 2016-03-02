package nl.tudelft.in4391.da.chat;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import java.rmi.*;
import java.rmi.server.*;

public class Chat implements ChatInterface  {

    public String name;
    public ChatInterface client=null;

    public Chat() throws RemoteException {

    }

    public Chat(String n)  throws RemoteException {
        this.name=n;
    }
    public String getName() throws RemoteException {
        return this.name;
    }

    public void setClient(ChatInterface c){
        client=c;
    }

    public ChatInterface getClient(){
        return client;
    }

    public void send(String s) throws RemoteException{
        System.out.println(s);
    }

    public String debug () throws RemoteException {
        return "DEBUG";
    }
}
