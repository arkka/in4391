package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Unit;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Arena {
    public Unit[][] unitCell = new Unit[25][25];
    public ArrayList<Unit> units = new ArrayList<Unit>();

    public Boolean spawnUnit(int x, int y, Unit unit) {
        if(unitCell[x][y] == null) {
            unitCell[x][y] = unit;
            addUnit(unit);
            return true;
        } else return false;
    }

    public Unit spawnUnitRandom(Unit unit) {
        Random rand = new Random();
        int x = 0;
        int y = 0;

        Boolean spawned = false;
        while(!spawned) {
            x = rand.nextInt(25);
            y = rand.nextInt(25);
            spawned = spawnUnit(x,y,unit);
        }
        unit.setCoord(x,y);
        return unit;
    }

    public Unit moveUnit(Unit unit, int x, int y) {
        // Check boundary
        // right
        if (unit.getX() == 24 && x >= 25){
            return unit;
        }
        // left
        if (unit.getX() == 0 && x < 0){
            return unit;
        }
        // up
        if (unit.getY() == 24 && y >= 25){
            return unit;
        }
        // down
        if (unit.getY() == 0 && y < 0){
            return unit;
        }

        // Check unit existence on next move
        if (unitCell[x][y] == null) {
            unitCell[x][y] = unit;
            unit.setCoord(x,y);
            return unit;
        }

        return unit;
    }

    public Unit removeUnit(Unit unit, int x, int y) {
        unitCell[x][y] = null;
        return unit;
    }

    public boolean checkSurrounding(Unit unit, int x, int y) {
        if (unitCell[x][y] != null){
            return false;
        }
        else {
            return true;
        }
    }

    public Unit getSurroundingUnit(int x, int y) {
        Unit adjacentUnit = unitCell[x][y];

//        if (adjacentUnit instanceof Dragon){
//            // do damage
//        }
//        else { // Knight
//            // heal player
//        }
        return adjacentUnit;
    }

    public void addUnit(Unit unit) {
        if(!units.contains(unit)) units.add(unit);
    }
}
