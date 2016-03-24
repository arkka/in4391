package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arkkadhiratara on 3/22/16.
 */
public class Arena implements Serializable {
    public Unit[][] unitCell = new Unit[25][25];
    public ArrayList<Unit> units = new ArrayList<Unit>();
    public ArrayList<Unit> knights = new ArrayList<Unit>();
    public ArrayList<Unit> dragons = new ArrayList<Unit>();

    public Boolean spawnUnit(int x, int y, Unit unit) {
        if(unitCell[x][y] == null) {
            unitCell[x][y] = unit;
            addUnit(unit);
            if(unit.getType().equals("dragon")) addDragon(unit);
            else addKnight(unit);
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

    public void deleteUnit(Unit unit) {
        unitCell[unit.getX()][unit.getY()] = null;
        units.remove(unit);
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
        unitCell[unit.getX()][unit.getY()] = unit;
    }

    public void addKnight(Unit unit) {
        if(!knights.contains(unit)) knights.add(unit);
    }

    public void addDragon(Unit unit) {
        if(!dragons.contains(unit)) dragons.add(unit);
    }

    public ArrayList<Unit> getDragons() {
        return this.dragons;
    }

    public void show() {
        for(int j=0;j<25;j++) {
            System.out.println("       ---------------------------------------------------");
            System.out.print("Row-"+j+" |");
            for(int i=0;i<25;i++) {
                Unit unit = unitCell[i][j];
                if(unit!=null) {
                    if(unit.getType().equals("dragon")) System.out.print("D");
                    else System.out.print("K");
                } else {
                    System.out.print(" ");
                }

                System.out.print("|");
            }
            System.out.println("\n       ---------------------------------------------------");
        }
    }
}
