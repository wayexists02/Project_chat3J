package chat3j.messages;

/**
 * 노드들 사이에서 오가는 메시지
 * 음성 데이터를 담고 있다.
 */
public class VoiceDataMsg extends Message {

    // data
    public byte[] data;
    public int size;

    public VoiceDataMsg() {

    }
}
