package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Dragon;
import nl.tudelft.in4391.da.unit.Knight;
import nl.tudelft.in4391.da.unit.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by arkkadhiratara on 3/22/16.
 */

public class Arena implements Serializable {

    public final static int MAP_WIDTH = 25;
    public final static int MAP_HEIGHT = 25;

    public Unit[][] unitCell = new Unit[MAP_WIDTH][MAP_HEIGHT];
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

    // Spawn unit on random location
    // Spawn unit with random hit points and attack points
    public Unit spawnUnitRandom(Unit unit) {
        Random rand = new Random();
        int x = 0;
        int y = 0;

        Boolean spawned = false;
        while(!spawned) {
            x = rand.nextInt(MAP_WIDTH);
            y = rand.nextInt(MAP_HEIGHT);
            spawned = spawnUnit(x, y, unit);
        }
        unit.setCoord(x,y);
        return unit;
    }

    public synchronized Unit moveUnit(Unit unit, int x, int y) {
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

    public synchronized Unit removeUnit(Unit unit, int x, int y) {
        unitCell[x][y] = null;
        return unit;
    }

    public synchronized void deleteUnit(Unit unit) {
        unitCell[unit.getX()][unit.getY()] = null;
//        unit.disconnect(); // thread
        units.remove(unit);
    }

    // Check surrounding for movings
    public boolean checkSurrounding(Unit unit, int x, int y) {
        if (unitCell[x][y] != null){
            return false;
        }
        else {
            return true;
        }
    }

    // Scan for unit nearby
    // Up to total 2 distance
    public Unit scanSurrounding(Unit unit) {
        Unit adjacentUnit = unitCell[unit.getX()][unit.getY()];

        scanUnit:
        for (int i = 0 ; i < 11 ; i++){
            // Get the nearby unit for at most 2 distance
            switch (i) {
                case 1:
                    // Get unit on the right
                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY()];
                    break;
                case 2:
                    // Get unit on the left
                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY()];
                    break;
                case 3:
                    // Get unit on the top
                    adjacentUnit = unitCell[unit.getX()][unit.getY() + 1];
                    break;
                case 4:
                    // Get unit on the bottom
                    adjacentUnit = unitCell[unit.getX()][unit.getY() - 1];
                    break;
                case 5:
                    // Get unit on the diagonal top left
                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY() + 1];
                    break;
                case 6:
                    // Get unit on the diagonal top right
                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY() + 1];
                    break;
                case 7:
                    // Get unit on the diagonal bottom left
                    adjacentUnit = unitCell[unit.getX() - 1][unit.getY() - 1];
                    break;
                case 8:
                    // Get unit on the diagonal bottom right
                    adjacentUnit = unitCell[unit.getX() + 1][unit.getY() - 1];
                    break;
                case 9:
                    // Get unit on the right distance 2
                    adjacentUnit = unitCell[unit.getX() + 2][unit.getY()];
                    break;
                case 10:
                    // Get unit on the left distance 2
                    adjacentUnit = unitCell[unit.getX() - 2][unit.getY()];
                    break;
                case 11:
                    // Get unit on the top distance 2
                    adjacentUnit = unitCell[unit.getX()][unit.getY() + 2];
                    break;
                case 12:
                    // Get unit on the bottom distance 2
                    adjacentUnit = unitCell[unit.getX()][unit.getY() - 2];
                    break;
            }

            if (adjacentUnit != null){
                break scanUnit;
            }

        }

        return adjacentUnit;
    }

    // After check whether surrounding empty or not when moving
    // If not, check the type of unit
    // Do damage if dragon
    // Heal if player
    public Unit actionToSurroundingUnit(Unit unit, int x, int y) {
        Unit adjacentUnit = unitCell[x][y];

        if (adjacentUnit.getType().equals("knight"))
        {
            // heal
            unit.healPlayer(adjacentUnit);

        } else { // Dragon
            // do damage
            unit.dealDamage(adjacentUnit);
        }

        return adjacentUnit;
    }

    public Boolean checkDead(Unit unit){
        boolean dead = false;
        if(unit.getHitPoints() <= 0) dead = true;

        return dead;
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
        for(int j=0;j<MAP_HEIGHT;j++) {
            System.out.println("       ---------------------------------------------------");
            System.out.print("Row-"+j+" |");
            for(int i=0;i<MAP_HEIGHT;i++) {
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
