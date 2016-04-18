package nl.tudelft.in4391.da;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class NodeEvent extends BaseEvent  {
    public static final Integer CONNECTED = 100;
    public static final Integer DISCONNECTED = 101;

    public NodeEvent(Node node) {
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
