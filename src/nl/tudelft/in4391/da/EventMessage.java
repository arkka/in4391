package nl.tudelft.in4391.da;

import java.io.*;
import java.util.UUID;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public class EventMessage implements Serializable {
    private UUID id;
    private int code;
    private Object object;
    private long requestNum;

    public EventMessage(int code, Object object) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.object = object;
        this.requestNum = requestNum;
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

    public boolean equals(Object c) {
        if(!(c instanceof EventMessage)) {
            return false;
        }

        EventMessage that = (EventMessage) c;
        return this.getId().equals(that.getId());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRequestNum(long rn) { this.requestNum = rn; }

    public long getRequestNum() { return this.requestNum; }

    public void increaseRequestNum() { this.requestNum++; }
}
