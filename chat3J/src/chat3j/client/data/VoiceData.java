package chat3j.client.data;

import com.esotericsoftware.kryo.Kryo;

/**
 * 오디오 데이터에 대한 클래스
 */
public class VoiceData extends Data<ByteArray> {

    final private static int DEFAULT = 44100;
    // 데이터가 저장되는 Array
    //private byte[] data;
    // 데이터가 얼마나 읽었는지를 나타냄
    private int numBytesRead;

    public VoiceData() {
        numBytesRead = 0;
        data.data = new byte[DEFAULT];
    }

    public VoiceData(final int SAMPLING_RATE_IN_HZ) {
        numBytesRead = 0;
        data.data = new byte[SAMPLING_RATE_IN_HZ];
    }

    @Override
    public void register(Kryo kryo) {
        kryo.register(byte[].class);
        kryo.register(VoiceData.class);
    }

    public int getNumBytesRead() {
        return numBytesRead;
    }

    public void setNumBytesRead(int numBytesRead) {
        this.numBytesRead = numBytesRead;
    }
}
