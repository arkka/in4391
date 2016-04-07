package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class PlayerEvent extends BaseEvent  {
    public static final Integer LOGGED_IN = 200;
    public static final Integer LOGGED_OUT = 201;

    public static final Integer UNIT_ATTACK = 400;
    public static final Integer UNIT_HEAL = 401;

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
            } else if (em.getCode() == UNIT_ATTACK) {
                onAttack(p);
            } else if (em.getCode() == UNIT_HEAL) {
                onHeal(p);
            }
        }
    }

    public void onLoggedIn(Player player){

    }

    public void onLoggedOut(Player player){

    }

    public void onAttack(Player player){

    }

    public void onHeal(Player player){

    }


}
