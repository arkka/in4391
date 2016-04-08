package nl.tudelft.in4391.da;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by sukmawicaksana on 4/5/2016.
 */

public class GameWorker {

	public ArrayList<Node> masterNodes;

	public GameWorker() {
		// Get the best master nodes
		masterNodes = new ArrayList<Node>();
		masterNodes.add(new Node(1, "127.0.0.1", 1100, 1200));
		masterNodes.add(new Node(2, "127.0.0.1", 1101, 1201));

		Server master = getMaster();


	}


	public static void main(String[] args)
	{

		// Parameter Arguments
		final Integer nodeId = (args.length < 1) ? Node.DEFAULT_NODE_ID : Integer.parseInt(args[0]);
		final Integer registry_port = (args.length < 2) ? Node.DEFAULT_REGISTRY_PORT : Integer.parseInt(args[1]);
		final Integer callback_port = (args.length < 3) ? Node.DEFAULT_CALLBACK_PORT : Integer.parseInt(args[2]);
		final Integer socket_port = (args.length < 4) ? Node.DEFAULT_SOCKET_PORT : Integer.parseInt(args[3]);

		// Initialize Current Node
		Node currentNode = new Node(nodeId,registry_port,callback_port,socket_port);

		// Initialize server object
		ServerImpl server = new ServerImpl(currentNode);




	}

	public Server getMaster(){

		Server bestServer = null;
		Node bestNode = null;

		long t = 0;
		long latency = 0;
		long maxLatency = 10000; // 10 seconds
		long bestLatency = maxLatency;

		// Ping all server and find the best latency
		for (Node n : masterNodes) {
			Server s = ServerImpl.fromRemoteNode(n);
			if(s!=null) {
				t = System.currentTimeMillis();

				try {
					if (s.ping()) {
						latency = System.currentTimeMillis() - t;
						System.out.println("[System] Game server " + n.getFullName() + " is available. ("+ latency +"ms)");
					}
				} catch (RemoteException e) {
					//e.printStackTrace();
					latency = maxLatency;
					System.out.println("[System] Game server " + n.getFullName() + " is down.");
				}
				n.setLatency(latency);

				if (latency < bestLatency) {
					bestLatency = latency;
					bestServer = s;
					bestNode = n;
				}
			}

		}

		if(bestServer!=null)
			System.out.println("[System] Connected to Game Server " + bestNode.getFullName() + ". ("+ bestLatency +"ms)");
		else
			System.out.println("[System] No available game server. Please try again later.");

		return bestServer;
	}

}
