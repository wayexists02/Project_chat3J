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
    private final Interrupt interrupt;

    public Communication(Publisher pub) {
        this.pub = pub;
        this.interrupt = new Interrupt();
        this.interrupt.ok = true;
    }

    public void start() {
        thread = new Thread(() -> {
            while (interrupt.ok) {
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
        interrupt.ok = false;
    }

    public abstract Data readData();
    public abstract boolean writeData(Data data);

    public enum ECommunicationType {
        VOICE, CHAT;
    }

    class Interrupt {
        public boolean ok;
    }
}
