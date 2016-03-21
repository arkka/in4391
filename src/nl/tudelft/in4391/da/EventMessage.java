package nl.tudelft.in4391.da;

import java.io.*;

/**
 * Created by arkkadhiratara on 3/21/16.
 */
public class EventMessage implements Serializable{
    private int code;
    private Object object;

    public EventMessage(int code, Object object) {
        this.code = code;
        this.object = object;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        return out.toByteArray();
    }
    public static EventMessage fromByte(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (EventMessage) is.readObject();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
