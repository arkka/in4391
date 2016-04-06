package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/21/16.
 */

import java.io.*;
import java.net.InetAddress;

/**
 * Created by arkkadhiratara on 3/11/16.
 */
public class Node implements Serializable {
    private Integer id;
    private String name;
    private String hostAddress;
    private Integer registryPort;
    private Integer callbackPort;
    private Integer socketPort;
    private long latency;

    Node(Integer id, String hostAddress, Integer registryPort, Integer callbackPort) {
        this.id = id;
        this.hostAddress = hostAddress;
        this.registryPort = registryPort;
        this.callbackPort = callbackPort;
    }

    Node(Integer id, String hostAddress, Integer registryPort, Integer callbackPort, Integer socketPort) {
        this.id = id;
        this.hostAddress = hostAddress;
        this.registryPort = registryPort;
        this.callbackPort = callbackPort;
        this.socketPort = socketPort;
    }

    public Integer getID() {
        return this.id;
    }

    public Integer getIndex() { return this.id-1; }

    public String getName() { return "Node-"+getID(); }

    public String getFullName() { return getName()+" ("+getHostAddress()+":"+getRegistryPort()+")"; }

    public String getHostAddress() {
        return this.hostAddress;
    }

    public Integer getCallbackPort() { return this.callbackPort; }

    public Integer getRegistryPort() {
        return registryPort;
    }

    public void setRegistryPort(Integer registryPort) {
        this.registryPort = registryPort;
    }

    public boolean equals(Object c) {
        if(!(c instanceof Node)) {
            return false;
        }

        Node that = (Node) c;
        return this.getID().equals(that.getID());
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }
}

