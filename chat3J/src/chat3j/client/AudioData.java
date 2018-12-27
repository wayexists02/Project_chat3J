package chat3j.client;

import com.esotericsoftware.kryo.Kryo;

/**
 * 오디오 데이터에 대한 클래스
 */
public class AudioData extends Data {

    final private int DEFAULT = 44100;
    // 데이터가 저장되는 Array
    private byte[] data;
    // 데이터가 얼마나 읽었는지를 나타냄
    private int numBytesRead;

    public AudioData() {
        numBytesRead = 0;
        data = new byte[DEFAULT];
    }

    public AudioData(final int SAMPLING_RATE_IN_HZ) {
        numBytesRead = 0;
        data = new byte[SAMPLING_RATE_IN_HZ];
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public void register(Kryo kryo) {
        kryo.register(byte[].class);
        kryo.register(AudioData.class);
    }

    public int getNumBytesRead() {
        return numBytesRead;
    }

    public void setNumBytesRead(int numBytesRead) {
        this.numBytesRead = numBytesRead;
    }
}
