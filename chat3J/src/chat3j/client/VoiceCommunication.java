package chat3j.client;

import chat3j.client.data.ByteArray;
import chat3j.client.data.Data;
import chat3j.client.data.VoiceData;
import chat3j.com.androidaudio.AudioLib.Player;
import chat3j.com.androidaudio.AudioLib.Recorder;

/**
 * 오디오를 입력받아서 녹음하고, 받은 오디오를 재생하여 출력해주는 클래스
 */
public class VoiceCommunication extends Communication {

    Recorder recorder;
    Player player;
    private int SAMPLING_RATE_IN_HZ;

    public VoiceCommunication(Publisher pub) {
        super(pub);
        this.SAMPLING_RATE_IN_HZ = 44100;
        recorder = new Recorder(SAMPLING_RATE_IN_HZ);
        player = new Player(SAMPLING_RATE_IN_HZ);
    }

    // Recorder로부터 AudioData를 읽어오는 메소드
    @Override
    public Data readData() {
        VoiceData data = new VoiceData(SAMPLING_RATE_IN_HZ);
        data.setNumBytesRead(recorder.read((data.getData()).data, 0, data.getData().data.length));
        return data;

        /*
        final int bufsize = SAMPLING_RATE_IN_HZ / 30;
        byte[] buf = new byte[bufsize];
        int n = recorder.read(buf, 0, bufsize);
        data.setData(buf);
        data.setNumBytesRead(n);
        return data;
        */
    }

    // Player로 AudioData를 재생하는 메소드
    @Override
    public boolean writeData(Data data) {
        VoiceData voiceData = (VoiceData) data;
        player.writeSamples(voiceData.getData().data, 0, voiceData.getNumBytesRead());
        return true;
    }
}
