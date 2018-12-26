package chat3j.client;

import chat3j.client.exceptions.ChatException;
import com.esotericsoftware.kryonet.Connection;

/**
 * 미구현
 */
public abstract class Communication {

    public static Communication getCommunicationInstance(String type) {
        if (type.equals("audio")) {
            AudioCommunication comm = new AudioCommunication();
            return comm;
        }
        else {
            return null;
        }
    }

    public void communicate(Connection conn) {
        Data data = readData();
        conn.sendTCP(data.getData());
    }

    public abstract boolean connect(Connection conn) throws ChatException;
    public abstract Data readData();
    public abstract boolean writeData(Data data);
}
