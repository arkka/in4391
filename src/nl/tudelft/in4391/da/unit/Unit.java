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

    protected Thread runnerThread;;

    // Health
    protected int maxHitPoints;
    protected int hitPoints;

    // Attack points
    protected int attackPoints;

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
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

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
}
