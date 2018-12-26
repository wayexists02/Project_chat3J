package chat3j.client;

import com.esotericsoftware.kryo.Kryo;

/**
 * 미구현
 */
public class AudioData extends Data {

    public short[] data;

    public AudioData() {

    }
    public AudioData(final int sampleSize, final int sendRate) {
        data = new short[(sampleSize / sendRate)];
    }

    @Override
    public short[] getData() {
        return data;
    }

    @Override
    public void register(Kryo kryo) {
        kryo.register(short[].class);
        kryo.register(AudioData.class);
    }
}
