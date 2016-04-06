package nl.tudelft.in4391.da;

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

    // Get player Unit
    public Unit getMyUnit(Player p){
        Unit u = p.getUnit();
        int i = units.indexOf(u);
        return units.get(i);
    }

    // Sync data
    public void syncUnits(){
        units = new ArrayList<Unit>();
        knights = new ArrayList<Unit>();
        dragons = new ArrayList<Unit>();

        for(int i=0;i<25;i++) {
            for (int j = 0; j < 25; j++) {
                Unit u = unitCell[i][j];
                if(u!=null) {
                    units.add(u);
                    if(u.getType().equals("Dragon")) dragons.add(u);
                    else knights.add(u);
                }
            }
        }
    }

    // Spawn unit on random location
    // Spawn unit with random hit points and attack points
    public Unit spawnUnit(Unit unit) {
        Random rand = new Random();
        Integer x;
        Integer y;

        while((unit.getX()==null)||(unit.getY()==null)) {
            x = rand.nextInt(25);
            y = rand.nextInt(25);

	        if(unitCell[x][y] == null) {
		        unit.setCoord(x,y);
		        addUnit(unit);
		        if(unit.getType().equals("Dragon")) addDragon(unit);
		        else addKnight(unit);
	        }

        }
        return unit;
    }

    public synchronized Unit moveUnit(Unit unit, int x, int y) {
        Integer last_x = unit.getX();
        Integer last_y = unit.getY();

        // Out of boundary?
        if ((x < 0 )|| (x > 25) || (y < 0) || (y > 25)) return unit;

        // Another unit exist?
        if (unitCell[x][y] != null) return unit;

        unit.setCoord(x,y);
        unitCell[x][y] = unit;

        // last step detected?
        if((last_x!=null)&&(last_y!=null)) unitCell[last_x][last_y] = null;

        return unit;
    }


    public void removeUnit(Unit unit) {
        if((unitCell[unit.getX()][unit.getY()]!=null) && (unit.getId().equals(unitCell[unit.getX()][unit.getY()].getId()))){
            unitCell[unit.getX()][unit.getY()] = null;
        }
    }

    public boolean checkSurrounding(Unit unit, int x, int y) {
        if (unitCell[x][y] != null){
            return false;
        }
        else {
            return true;
        }
    }

    // After check whether surrounding empty or not when moving
    // If not, check the type of unit
    // Do damage if dragon
    // Heal if player
    public Unit actionToSurroundingUnit(Unit unit, int x, int y) {
        Unit adjacentUnit = unitCell[x][y];

        if (adjacentUnit.getType().equals("Knight"))
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
        for(int j=0;j<25;j++) {
            System.out.println("       ---------------------------------------------------");
            System.out.print("Row-"+j+" |");
            for(int i=0;i<25;i++) {
                Unit unit = unitCell[i][j];
                if(unit!=null) {
                    if(unit.getType().equals("Dragon")) System.out.print("D");
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
