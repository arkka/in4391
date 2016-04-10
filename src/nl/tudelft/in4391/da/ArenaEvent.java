package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/7/2016.
 */

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class ArenaEvent extends BaseEvent  {

	public static final Integer UPDATE = 500;

	public ArenaEvent(Node node) {
		super(node);
	}

	// Event splitter
	@Override
	public void onEvent(EventMessage em) {
		if(em.getCode() == UPDATE ) {
			onUpdated(em);
		}
	}

	public void onUpdated(EventMessage em){

	}
}
