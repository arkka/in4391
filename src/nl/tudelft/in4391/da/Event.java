package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 4/5/16.
 */
public interface Event {
    void onReceiveData(byte[] receiveData, int length);
}
