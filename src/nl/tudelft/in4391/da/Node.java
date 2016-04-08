package nl.tudelft.in4391.da;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class Node implements Serializable {
    static Integer DEFAULT_NODE_ID = 1;
    static String DEFAULT_NODE_NAME = "Node";
    static Integer DEFAULT_REGISTRY_PORT = 1100;
    static Integer DEFAULT_CALLBACK_PORT = 1200;
    static Integer DEFAULT_SOCKET_PORT = 1300;
    static String DEFAULT_MULTICAST_GROUP = "239.255.1.113";

    static Integer TYPE_MASTER = 10;
    static Integer TYPE_WORKER = 20;

    static Integer STATUS_DEAD = 10;
    static Integer STATUS_BUSY = 20;
    static Integer STATUS_READY = 30;


    // Node Attribute

    private Integer id;
    private String name;
    private String hostAddress;
    private Integer registryPort;
    private Integer callbackPort;
    private Integer socketPort;
    private String multicastGroup;
    private InetAddress multicastGroupAddress;
    private Integer type;

    // Scheduler attributes
    private long latency;
    private long requestNum = 0;
    private int status = STATUS_READY;


    public Node(Integer id, Integer registryPort, Integer callbackPort){
        this(id, DEFAULT_NODE_NAME, "127.0.0.1", registryPort, callbackPort, DEFAULT_SOCKET_PORT, DEFAULT_MULTICAST_GROUP);
    }

    public Node(Integer id, Integer registryPort, Integer callbackPort, Integer socketPort){
        this(id, DEFAULT_NODE_NAME, "127.0.0.1", registryPort, callbackPort, socketPort, DEFAULT_MULTICAST_GROUP);
    }

    public Node(Integer id, String name, Integer registryPort, Integer callbackPort){
        this(id, name, "127.0.0.1", registryPort, callbackPort, DEFAULT_SOCKET_PORT, DEFAULT_MULTICAST_GROUP);
    }

    public Node(Integer id, String name, String hostAddress, Integer registryPort, Integer callbackPort){
        this(id, name, hostAddress, registryPort, callbackPort, DEFAULT_SOCKET_PORT, DEFAULT_MULTICAST_GROUP);
    }


    public Node(Integer id, String name, String hostAddress, Integer registryPort, Integer callbackPort, Integer socketPort, String multicastGroup){
        this.id = id;
        this.name = name;
        this.hostAddress = hostAddress;
        this.name = DEFAULT_NODE_NAME+"-"+id;
        try {
            this.multicastGroupAddress = InetAddress.getByName(multicastGroup);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.registryPort = registryPort;
        this.callbackPort = callbackPort;
        this.socketPort = socketPort;
        this.multicastGroup = multicastGroup;
        this.type = TYPE_MASTER;
    }

    public Integer getId() { return id; }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return name+" ( "+hostAddress+":"+registryPort+" )";
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Integer getRegistryPort() {
        return registryPort;
    }

    public Integer getCallbackPort() {
        return callbackPort;
    }

    public Integer getSocketPort() {
        return socketPort;
    }

    public String getMulticastGroup() {
        return multicastGroup;
    }

    public InetAddress getMulticastGroupAddress() {
        return multicastGroupAddress;
    }

    public boolean equals(Object c) {
        if(!(c instanceof Node)) {
            return false;
        }

        Node that = (Node) c;
        return this.getId().equals(that.getId());
    }


    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public long getRequestNum() {
        return requestNum;
    }

    public void setRequestNum(long requestNum) {
        this.requestNum = requestNum;
    }

    public void increaseRequestNum() {
        this.requestNum++;
    }

    public int getStatus() {
        return status;
    }
}
