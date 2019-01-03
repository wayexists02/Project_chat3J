package chat3j.client.data;

import chat3j.ByteArrayData;

/**
 * 오디오 데이터에 대한 클래스
 */
public class VoiceData extends Data {

    final private static int DEFAULT = 44100;
    // 데이터가 저장되는 Array
    //private byte[] data;
    // 데이터가 얼마나 읽었는지를 나타냄
    private int numBytesRead;

    public VoiceData() {
        numBytesRead = 0;
        ByteArrayData bData = new ByteArrayData();
        bData.data = new byte[DEFAULT];
        this.data = bData;
    }

    public VoiceData(final int SAMPLING_RATE_IN_HZ) {
        numBytesRead = 0;
        ByteArrayData bData = new ByteArrayData();
        bData.data = new byte[SAMPLING_RATE_IN_HZ / 25];
        this.data = bData;
    }

    public int getNumBytesRead() {
        return numBytesRead;
    }

    public void setNumBytesRead(int numBytesRead) {
        this.numBytesRead = numBytesRead;
    }
}
