package nl.tudelft.in4391.da;

import nl.tudelft.in4391.da.unit.Unit;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class PlayerEvent extends BaseEvent  {
    public static final Integer LOGGED_IN = 200;
    public static final Integer LOGGED_OUT = 201;

    public static final Integer UNIT_SPAWNED = 300;
    public static final Integer UNIT_MOVED = 301;

    public PlayerEvent(Node node) {
        super(node);
    }

    // Event splitter
    @Override
    public void onEvent(EventMessage em) {
        if(em.getObject() instanceof Player) {
            Player p = (Player) em.getObject();
            if (em.getCode() == LOGGED_IN) {
                onLoggedIn(p);
            } else if (em.getCode() == LOGGED_OUT) {
                onLoggedOut(p);
            }

            Unit u = (Unit) em.getObject();
            if (em.getCode() == UNIT_SPAWNED) {
                onUnitSpawned(u);
            } else if (em.getCode() == UNIT_MOVED) {
                onUnitMoved(u);
            }
        }
    }

    public void onLoggedIn(Player player){

    }

    public void onLoggedOut(Player player){

    }

    public void onUnitSpawned(Unit unit){

    }

    public void onUnitMoved(Unit unit){

    }


}
