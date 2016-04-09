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
	public static final Integer UNIT_MOVE_UP = 401;
	public static final Integer UNIT_MOVE_DOWN = 402;
	public static final Integer UNIT_MOVE_RIGHT = 403;
	public static final Integer UNIT_MOVE_LEFT = 404;


	public static final Integer UNIT_ATTACK = 500;
	public static final Integer UNIT_HEAL = 600;

	public UnitEvent(Node node) {
		super(node);
	}

	// Event splitter
	@Override
	public void onEvent(EventMessage em) {
		if ( em.getObject() instanceof Unit ) {
			Unit unit = (Unit) em.getObject();
			onNewEvent(em.getCode(),unit);
		}
	}

	public void onNewEvent(Integer code, Unit unit){

	}
}
