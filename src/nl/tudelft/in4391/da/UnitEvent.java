package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/7/2016.
 */

import nl.tudelft.in4391.da.unit.Unit;
import java.util.ArrayList;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class UnitEvent extends BaseEvent  {

	public static final Integer UNIT_MOVE = 400;
	public static final Integer UNIT_ATTACK = 401;
	public static final Integer UNIT_HEAL = 402;

	public UnitEvent(Node node) {
		super(node);
	}

	// Event splitter
	@Override
	public void onEvent(EventMessage em) {
		if ( em.getObject() instanceof ArrayList ) {
			ArrayList<Unit> us = (ArrayList<Unit>) em.getObject();
			if (em.getCode() == UNIT_MOVE) {
				onMove(us.get(0),  us.get(1));
			} else if (em.getCode() == UNIT_ATTACK) {
				onAttack(us.get(0),  us.get(1));
			} else if (em.getCode() == UNIT_HEAL) {
				onHeal(us.get(0),  us.get(1));
			}
		}
	}

	public void onMove(Unit u, Unit t){

	}

	public void onAttack(Unit u, Unit t){

	}

	public void onHeal(Unit u, Unit t){

	}


}
