package nl.tudelft.in4391.da;

/**
 * Created by arkkadhiratara on 3/9/16.
 */
public interface EventListener {
    public void onReceiveData(byte[] receiveData, int length);
}
