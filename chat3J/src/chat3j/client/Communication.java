package chat3j.client;

import chat3j.client.data.ByteArray;
import chat3j.client.data.Data;
import chat3j.messages.VoiceDataMsg;

/**
 * 미구현
 */
public abstract class Communication {

    private Thread thread;
    private Publisher pub;

    public Communication(Publisher pub) {
        this.pub = pub;
    }

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                Data data = readData();
                if (data.getData() == null || ((ByteArray) data.getData()).data.length <= 0) {
                    Thread.yield();
                }
                else {
                    VoiceDataMsg msg = new VoiceDataMsg();
                    msg.data = ((ByteArray) data.getData()).data;
                    msg.size = ((ByteArray) data.getData()).data.length;
                    pub.broadcast(msg);
                }
            }
        });

        thread.start();
    }

    public void interrupt() {
        thread.interrupt();
    }

    public abstract Data readData();
    public abstract boolean writeData(Data data);

    public enum ECommunicationType {
        VOICE, CHAT;
    }
}
