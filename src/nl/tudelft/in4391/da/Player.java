package nl.tudelft.in4391.da;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Created by arkkadhiratara on 3/6/16.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 6196049233420585921L;

    private UUID id;
    private String username;
    //private String password; // unimplemented
    private String hostAddress;
    private boolean isAuthenticated = false;

    private int level;
    private int experience;

    public Player(String username, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
    }
    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
        this.id = id;
    }

    public void setHostAddress(String hostAddress){
        this.hostAddress = hostAddress;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    public Boolean isAuthenticated(){
        return this.isAuthenticated;
    }

    public String getUsername() {
        return this.username;
    }

    public String toString() {
        return this.username+" ( "+this.hostAddress+" )";
    }

    public boolean equals(Object c) {
        if(!(c instanceof Player)) {
            return false;
        }

        Player that = (Player) c;
        return this.getID().equals(that.getID());
    }


}
