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

    protected Thread runnerThread;;

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
        this.hitPoints = hitPoints;
    }

    public void setAttackPoints(int attackPoints) {
        this.attackPoints = attackPoints;
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

    public synchronized void adjustHitPoints(int modifier){
        if (hitPoints <= 0)
            return;

        this.hitPoints += modifier;

        // Adjust hitPoints if exceed maximal
        // Case of player heal
//        if (hitPoints > maxHitPoints)
//            hitPoints = maxHitPoints;

        // Remove unit if dies
        // Case of damage dealt
//        if (hitPoints <= 0)
//            deleteUnit(x, y);
    }

    public void healPlayer(Unit adjacentUnit) {
        int hpAfterAttack = adjacentUnit.getHitPoints() + this.attackPoints;

        // Set maximal HP after heal to the random max HP for each Knight
        if (hpAfterAttack >= adjacentUnit.getMaxHitPoints()) hpAfterAttack = adjacentUnit.getMaxHitPoints();

        adjacentUnit.setHitPoints(hpAfterAttack);
    }

    public void dealDamage(Unit adjacentUnit) {
        int hpAfterAttack = adjacentUnit.getHitPoints() - this.attackPoints;

    //        if (hpAfterAttack <= 0){
    //
    //        } else { // Dragon still survive
    //            adjacentUnit.setHitPoints(hpAfterAttack);
    //        }

        adjacentUnit.setHitPoints(hpAfterAttack);
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
