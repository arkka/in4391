package nl.tudelft.in4391.da.unit;

/**
 * Created by arkkadhiratara on 3/22/16.
 */

public class Knight extends Unit {

    // The minimum and maximum amount of hitpoints that a particular player starts with
    public static final int MIN_HITPOINTS = 10;
    public static final int MAX_HITPOINTS = 20;
    // The minimum and maximum amount of attackpoints that a particular player has
    public static final int MIN_ATTACKPOINTS = 1;
    public static final int MAX_ATTACKPOINTS = 10;

    public Knight(String name) {
        super(name, "knight");

        // Initialize hitpoints and attackpoints for each Knight
        this.hitPoints = (int) (Math.random() * (MAX_HITPOINTS - MIN_HITPOINTS) + MIN_HITPOINTS);
        this.attackPoints = (int) (Math.random() * (MAX_ATTACKPOINTS - MIN_ATTACKPOINTS) + MIN_ATTACKPOINTS);

        // Assign max health for each Knight
        this.maxHitPoints = this.hitPoints;
    }
}
