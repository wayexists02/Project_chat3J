package chat3j.client;

import chat3j.client.exceptions.AudioConnectException;
import chat3j.client.exceptions.ChatException;
import com.esotericsoftware.kryonet.Connection;

/**
 * 미구현
 */
public class AudioCommunication extends Communication {

    private int sampleRate;
    private int sendRate;
    private boolean isMono;

    public AudioCommunication() {
        this.sampleRate = 44100;
        this.sendRate = 30;
        this.isMono = false;
    }

    @Override
    public boolean connect(Connection conn) throws ChatException {
        return false;
    }

    @Override
    public Data readData() {
        return null;
    }

    @Override
    public boolean writeData(Data data) {
        return false;
    }
}
