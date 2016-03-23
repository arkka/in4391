package nl.tudelft.in4391.da.unit;

import java.io.Serializable;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Unit implements Serializable {
    String name;
    String type;
    int x;
    int y;

    public Unit(String name) {
        this.name = name;
    }

    public Unit(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }
    public void setCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() { return this.type; }
}
