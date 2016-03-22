package nl.tudelft.in4391.da.unit;

import nl.tudelft.in4391.da.Player;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Knight extends Unit {
    Player player;

    public Knight(Player player) {

    }

    @Override
    public String toString() {
        return "Knight "+player.getUsername();
    }
}
