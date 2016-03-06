package nl.tudelft.in4391.da;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by arkkadhiratara on 3/6/16.
 */
public class Player implements Serializable {
    private static final long serialVersionUID = 6196049233420585921L;

    private UUID id;
    private String username;
    private String hostAddress;

    private int level;
    private int experience;

    public Player(String username, String hostAddress) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.hostAddress = hostAddress;
    }

    public String toString() {
        return this.username+" ( "+this.hostAddress+" )";
    }
}
