package nl.tudelft.in4391.da;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * Created by arkkadhiratara on 3/7/16.
 */
public class Event extends Thread {
    public static final Integer NODE_CONNECTED = 100;
    public static final Integer NODE_DISCONNECTED = 101;
    public static final Integer NODE_MASTER_ELECTION =105;
    public static final Integer PLAYER_CONNECTED = 200;
    public static final Integer PLAYER_DISCONNECTED = 201;
    public static final Integer UNIT_SPAWN = 300;
    public static final Integer UNIT_DEAD = 301;
    public static final Integer UNIT_MOVED = 302;
    public static final Integer UNIT_REMOVED = 303;

    byte[] message;
    int socketPort;

    InetAddress groupAddress;
    MulticastSocket socket = null;
    BufferedReader in = null;
    boolean listening = true;

    public int dataLength = 1024; // DEFAULT

    EventListener listener;


    public Event(String groupAddressName, int socketPort) throws IOException {
        this.socketPort = socketPort;
        this.groupAddress = InetAddress.getByName(groupAddressName);
        this.socket = new MulticastSocket(socketPort);

        // Only mac problem?
        this.socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));

        // Join Group
        this.socket.joinGroup(this.groupAddress);
    }

    public void run() {
        try {
            // RECEIVE MULTICAST
            byte[] receiveData = new byte[dataLength];
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            while (listening) {
                this.socket.receive(packet);
                listener.onReceiveData(receiveData,packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(int code, Object obj) {
        EventMessage message = new EventMessage(code,obj);
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.groupAddress, this.socketPort);
            this.socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setListener( EventListener listener ){
        this.listener = listener;
    }


}

