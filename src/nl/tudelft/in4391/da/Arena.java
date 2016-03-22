package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;

import java.util.Random;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Arena {
    public Unit[][] unitCell = new Unit[25][25];

    public Boolean spawnUnit(int x, int y, Unit unit) {
        if(unitCell[x][y] == null) {
            unitCell[x][y] = unit;
            return true;
        } else return false;
    }

    public void spawnUnitRandom(Unit unit) {
        Random rand = new Random();
        if(!spawnUnit(rand.nextInt(25),rand.nextInt(25),unit)) {
            spawnUnitRandom(unit); // try again :)
        }

    }
}
