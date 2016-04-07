package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class PlayerEvent extends BaseEvent  {
    public static final Integer LOGGED_IN = 200;
    public static final Integer LOGGED_OUT = 201;
    public static final Integer ATTACK = 202;
    public static final Integer HEAL = 203;

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
            } else if (em.getCode() == ATTACK) {
                onAttack(p);
            } else if (em.getCode() == HEAL) {
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
