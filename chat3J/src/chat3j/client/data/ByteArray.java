package chat3j.client.data;

/**
 * 바이트 배열을 저장하는 객체
 */
public class ByteArray {

    public byte[] data;

    public ByteArray() {

    }
    public ByteArray(final int size) {
        data = new byte[size];
    }
}
