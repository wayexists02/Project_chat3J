package com.chat3j.core.client;

import com.chat3j.core.client.data.ByteArray;
import com.chat3j.core.client.data.Data;
import com.chat3j.core.messages.VoiceDataMsg;

/**
 * 미구현
 */
public abstract class Communication {

    protected Thread thread;
    protected Publisher pub;
    protected final Interrupt interrupt;

    public Communication(Publisher pub) {
        this.pub = pub;
        this.interrupt = new Interrupt();
        this.interrupt.ok = true;
    }

    public void start() {
        thread = new Thread() {
            @Override
            public void run() {
                while (interrupt.ok) {
                    System.out.println("READ DATA");
                    Data data = readData();
                    if (data.getData() == null || ((ByteArray) data.getData()).data.length <= 0) {
                        Thread.yield();
                    } else {
                        VoiceDataMsg msg = new VoiceDataMsg();
                        msg.type = "Voice";
                        msg.data = ((ByteArray) data.getData()).data;
                        msg.size = ((ByteArray) data.getData()).data.length;
                        System.out.println("Voice Data Msg is Sending - size : " + msg.size + " / HashCode : "+msg.data.hashCode());
                        pub.broadcast(msg);
                    }
                }
            }
        };

        thread.start();
    }

    public Publisher getPub() {
        return pub;
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

