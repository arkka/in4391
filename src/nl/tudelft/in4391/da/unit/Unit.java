package nl.tudelft.in4391.da.unit;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Unit implements Serializable {
    private UUID id;

    String name;
    String type;
    Integer x;
    Integer y;

    protected Thread runnerThread;
    protected boolean running;

    // Turn delay
    protected int timeBetweenTurns;

    // Health
    protected Integer maxHitPoints;
    protected Integer hitPoints;

    // Attack points
    protected Integer attackPoints;

    public Unit(String name) {
        this.name = name;
    }

    public Unit(String name, String type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.type = type;
        this.x = null;
        this.y = null;

    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getFullName() { return this.type+" "+this.name; }

    public void setCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setHitPoints(int hitPoints) {
        if (hitPoints > this.getMaxHitPoints()) {
            this.hitPoints = this.getMaxHitPoints();
        } else {
            this.hitPoints = hitPoints;
        }
    }

    public int getHitPoints() {
        return hitPoints;
    }

    // Max HP for after heal and for status of user
    public Integer getMaxHitPoints() {
        return maxHitPoints;
    }

    public Integer getAttackPoints() {
        return attackPoints;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() { return y; }

    public int getTurnDelay(){
        return timeBetweenTurns;
    }

    public String getType() {
        return this.type;
    }

    public boolean equals(Object c) {
        if(!(c instanceof Unit)) {
            return false;
        }

        Unit that = (Unit) c;
        return this.getId().equals(that.getId());
    }
}
