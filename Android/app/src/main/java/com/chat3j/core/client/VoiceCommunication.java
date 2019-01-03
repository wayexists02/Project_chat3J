package com.chat3j.core.client;

import com.chat3j.android.Player;
import com.chat3j.android.Recorder;
import com.chat3j.core.client.data.Data;
import com.chat3j.core.client.data.VoiceData;

/**
 * 오디오를 입력받아서 녹음하고, 받은 오디오를 재생하여 출력해주는 클래스
 */
public class VoiceCommunication extends Communication {

    Recorder recorder;
    Player player;
    private int SAMPLING_RATE_IN_HZ;

//    private TargetDataLine targetLine;
//    private SourceDataLine sourceLine;

    public VoiceCommunication(Publisher pub) {
        super(pub);
        this.SAMPLING_RATE_IN_HZ = 44100;

        recorder = new Recorder(SAMPLING_RATE_IN_HZ);
        player = new Player(SAMPLING_RATE_IN_HZ);

        recorder.start();
        player.start();

        /* 데스크탑 테스트용 */
//        AudioFormat format = new AudioFormat(SAMPLING_RATE_IN_HZ, 16, 2, true, true);
//
//        DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
//        DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
//
//        try {
//            targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
//            targetLine.open(format);
//            targetLine.start();
//
//            sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
//            sourceLine.open(format);
//            sourceLine.start();
//
//        } catch (Exception exc) {
//
//        }
    }

    // Recorder로부터 AudioData를 읽어오는 메소드
    @Override
    public Data readData() {

        VoiceData data = new VoiceData(SAMPLING_RATE_IN_HZ);
        int length = recorder.read(data.getData().data, 0, data.getData().data.length);
        data.setNumBytesRead(length);
        return data;


        /* 데스크탑 테스트용 */
//        VoiceData data = new VoiceData(SAMPLING_RATE_IN_HZ);
//        final int bufsize = SAMPLING_RATE_IN_HZ;
//        ByteArray ba = new ByteArray(bufsize);
//        int n = targetLine.read(ba.data, 0, bufsize);
//        data.setData(ba);
//        data.setNumBytesRead(n);
//        return data;

    }

    // Player로 AudioData를 재생하는 메소드
    @Override
    public boolean writeData(Data data) {

        VoiceData voiceData = (VoiceData) data;
        player.writeSamples(voiceData.getData().data, 0, voiceData.getNumBytesRead());
        return true;


        /* 데스크탑에서 테스트용 */
//        VoiceData vdata = (VoiceData) data;
//        sourceLine.write(vdata.getData().data, 0, SAMPLING_RATE_IN_HZ);
//        return true;

    }
}
