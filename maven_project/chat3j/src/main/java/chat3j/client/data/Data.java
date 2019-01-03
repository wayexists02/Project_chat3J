package chat3j.client.data;

import com.esotericsoftware.kryo.Kryo;

/**
 * 데이터의 최상위 클래스
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
