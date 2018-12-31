package chat3j.client.data;

import com.esotericsoftware.kryo.Kryo;

/**
 * 미구현
 */
public abstract class Data<T> {

    protected T data;

    public abstract void register(Kryo kryo);

    public void setData(T data) {
        this.data = data;
    }
    public T getData() {
        return data;
    }
}
