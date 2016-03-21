package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/21/16.
 */

import java.io.*;

/**
 * Created by arkkadhiratara on 3/11/16.
 */
public class Node implements Serializable {
    private Integer id;
    private String name;
    private String hostAddress;
    private Integer registryPort;
    private Integer callbackPort;

    Node(Integer id, String hostAddress, Integer registryPort, Integer callbackPort) {
        this.id = id;
        this.hostAddress = hostAddress;
        this.registryPort = registryPort;
        this.callbackPort = callbackPort;
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

    public static byte[] serialize(Node obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Node deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (Node) is.readObject();
    }

    public boolean equals(Object c) {
        if(!(c instanceof Node)) {
            return false;
        }

        Node that = (Node) c;
        return this.getID().equals(that.getID());
    }
}

