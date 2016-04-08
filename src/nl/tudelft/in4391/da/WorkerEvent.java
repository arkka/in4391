package nl.tudelft.in4391.da;

/**
 * Created by sukmawicaksana on 4/8/2016.
 */
public class WorkerEvent extends BaseEvent  {
	public static final Integer CONNECTED = 100;
	public static final Integer DISCONNECTED = 101;

	public WorkerEvent(Node node) {
		super(node);
	}

	// Event splitter
	@Override
	public void onEvent(EventMessage em) {
		if(em.getObject() instanceof Node) {
			Node n = (Node) em.getObject();
			if (em.getCode() == CONNECTED) {
				onConnected(n);
			} else if (em.getCode() == DISCONNECTED) {
				onDisconnected(n);
			}
		}
	}

	public void onConnected(Node emNode) {
	}

	public void onDisconnected(Node emNode) {
	}
}
