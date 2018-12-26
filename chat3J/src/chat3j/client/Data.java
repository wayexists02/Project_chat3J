package chat3j.client;

import com.esotericsoftware.kryo.Kryo;

/**
 * 미구현
 */
public abstract class Data {

    public abstract void register(Kryo kryo);
    public abstract <T> T getData();
}
