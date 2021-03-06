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
		if(em.getCode() == UNIT_MOVE || em.getCode() == UNIT_ATTACK || em.getCode() == UNIT_HEAL ) {
			onNewEvent(em);
		}
	}

	public void onNewEvent(EventMessage em){

	}
}
