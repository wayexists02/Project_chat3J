package chat3j.client;

import chat3j.client.exceptions.ChatException;
import chat3j.com.androidaudio.AudioLib.Player;
import chat3j.com.androidaudio.AudioLib.Recorder;
import com.esotericsoftware.kryonet.Connection;

/**
 * 오디오를 입력받아서 녹음하고, 받은 오디오를 재생하여 출력해주는 클래스
 */
public class AudioCommunication extends Communication {

    Recorder recorder;
    Player player;
    private int SAMPLING_RATE_IN_HZ;

    public AudioCommunication() {
        this.SAMPLING_RATE_IN_HZ = 44100;
        recorder = new Recorder(SAMPLING_RATE_IN_HZ);
        player = new Player(SAMPLING_RATE_IN_HZ);
    }

    @Override
    public boolean connect(Connection conn) throws ChatException {
        return false;
    }

    // Recorder로부터 AudioData를 읽어오는 메소드
    @Override
    public Data readData() {
        AudioData data = new AudioData(SAMPLING_RATE_IN_HZ);
        data.setNumBytesRead(recorder.read(data.getData(), 0, data.getData().length));
        return data;
    }

    // Player로 AudioData를 재생하는 메소드
    @Override
    public boolean writeData(Data data) {
        AudioData audioData = (AudioData) data;
        player.writeSamples(audioData.getData(), 0, audioData.getNumBytesRead());
        return true;
    }
}
