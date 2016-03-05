package nl.tudelft.in4391.da.chat;

/**
 * Created by arkkadhiratara on 3/2/16.
 */
import nl.tudelft.in4391.da.User;

import java.rmi.*;

public class Chat implements ChatInterface  {

    public User user;
    public ChatInterface client=null;

    public Chat(User user) throws RemoteException {
        this.user = user;
    }

    public User getUser() throws RemoteException {
        return this.user;
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
