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
public class Multicast extends Thread {
    byte[] message;
    int socketPort;

    InetAddress groupAddress;
    MulticastSocket socket = null;
    BufferedReader in = null;
    boolean listening = true;

    public int dataLength = 1024; // DEFAULT

    MulticastListener listener;


    public Multicast(byte[] message, String groupAddressName, int socketPort) throws IOException {
        // Init Multicast & Join Group
        this.message = message;
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
            byte[] receiveData = new byte[dataLength]; // TO-DO set correct size
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            while (listening) {
                this.socket.receive(packet);
                //System.out.println("Received Multicast: " + (new String(receiveData, 0, packet.getLength())));
                listener.onReceiveData(receiveData,packet.getLength());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send() throws IOException {
        // SEND MULTICAST
        //System.out.println("Send Multicast: " + name);
        DatagramPacket sendPacket = new DatagramPacket(this.message, this.message.length, this.groupAddress, this.socketPort);
        this.socket.send(sendPacket);
    }
    public void setListener( MulticastListener listener ){
        this.listener = listener;
    }

}

