package chat3j.client;

import chat3j.ByteArrayData;
import chat3j.Chat3JSourceDevice;
import chat3j.Chat3JTargetDevice;
import chat3j.CommunicationData;
import chat3j.client.data.Data;
import chat3j.client.data.VoiceData;
import chat3j.messages.VoiceDataMsg;
import chat3j.utils.Logger;

/**
 * 오디오를 입력받아서 녹음하고, 받은 오디오를 재생하여 출력해주는 클래스
 */
public class VoiceCommunication extends Communication {

    /*
    Recorder recorder;
    Player player;
    */

    private int SAMPLING_RATE_IN_HZ;

    /*
    public VoiceCommunication(Publisher pub) {
        super(pub);
        this.SAMPLING_RATE_IN_HZ = 44100;
        recorder = new Recorder(SAMPLING_RATE_IN_HZ);
        player = new Player(SAMPLING_RATE_IN_HZ);
    }
    */

    //수정
    public VoiceCommunication(Publisher pub, Chat3JSourceDevice source, Chat3JTargetDevice target) {
        super(pub, source, target);
    }

    // Recorder로부터 AudioData를 읽어오는 메소드
    @Override
    public Data readData() {

        /*
        VoiceData data = new VoiceData(SAMPLING_RATE_IN_HZ);
        data.setNumBytesRead(recorder.read((data.getData()).data, 0, data.getData().data.length));
        return data;
        */


        //수정
        if (target  == null) {
            Logger.getLogger().error("Target device must be set");
            return null;
        }

        try {
            ByteArrayData commData = (ByteArrayData) target.readData();
            VoiceData vData = new VoiceData();
            vData.setData(commData);
            return vData;
        } catch (ClassCastException exc) {
            Logger.getLogger().error("Voice data type must be ByteArrayData");
            return null;
        }
    }

    // Player로 AudioData를 재생하는 메소드
    @Override
    public boolean writeData(Data data) {

        /*
        VoiceData voiceData = (VoiceData) data;
        player.writeSamples(voiceData.getData().data, 0, voiceData.getNumBytesRead());
        return true;
        */

        // 수정
        if (source == null) {
            Logger.getLogger().error("Source device must be set");
            return false;
        }

        try {
            VoiceData vData = (VoiceData) data;
            source.writeData(vData.getData());
            return true;
        } catch (ClassCastException exc) {
            Logger.getLogger().error("Voice communication can handle only VoiceData");
            return false;
        }
    }

    @Override
    public void start() {
        thread = new Thread(() -> {
            while (interrupt.ok) {
                VoiceData data = (VoiceData) readData();
                if (((ByteArrayData) data.getData()).size <= 0) {
                    Thread.yield();
                    continue;
                }

                ByteArrayData cData = (ByteArrayData) data.getData();
                VoiceDataMsg msg = new VoiceDataMsg();
                msg.type = "Voice";
                msg.data = cData.data;
                msg.size = cData.size;

                if (echo)
                    writeData(data);
                pub.broadcast(msg);
            }
        });

        thread.start();
    }
}
