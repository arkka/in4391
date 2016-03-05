package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/5/16.
 */
public class User {
    private String username;

    public User(String username){
        this.username = username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
