package nl.tudelft.in4391.da;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class BaseEvent extends Thread implements Event  {
    public static final Integer CONNECTED = 100;
    public static final Integer DISCONNECTED = 101;

    public static Integer DEFAULT_EVENT_DATA_LENGTH = 1024;

    private Node node;
    private MulticastSocket socket;
    boolean listen = true;

    public BaseEvent(Node node) {
        this.node = node;
        try {
            // Initialize multicast socket
            this.socket = new MulticastSocket(node.getSocketPort());

            // Mac development compatibility
            this.socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));

            // Join Group
            this.socket.joinGroup(InetAddress.getByName(node.getMulticastGroup()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {

                byte[] receiveData = new byte[DEFAULT_EVENT_DATA_LENGTH];
                DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            while (listen) {
                this.socket.receive(packet);
                onReceiveData(receiveData,packet.getLength());

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        // Run listener
        this.start();
    }

    public void send(int code, Object obj) {
        EventMessage message = new EventMessage(code,obj);
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.node.getMulticastGroupAddress(), this.node.getSocketPort());
            this.socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(EventMessage message) {
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.node.getMulticastGroupAddress(), this.node.getSocketPort());
            this.socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onReceiveData(byte[] receiveData, int length) {
        try {
            EventMessage em = EventMessage.fromByte(receiveData);
            onEvent(em);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onEvent(EventMessage e) {

    }
}
