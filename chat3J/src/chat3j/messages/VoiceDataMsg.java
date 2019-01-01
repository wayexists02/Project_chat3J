package chat3j.messages;

/**
 * 노드들 사이에서 오가는 메시지
 * 음성 데이터를 담고 있다.
 */
public class VoiceDataMsg extends CommunicationDataMsg {

    // data
    public byte[] data;
    public int size;

    public VoiceDataMsg() {

    }

    /**
     * 데이터를 리턴함
     * @return 데이터
     */
    @Override
    public Object getData() {
        return data;
    }

    /**
     * 데이터를 이 메시지 객체에 저장
     * @param obj
     */
    @Override
    public void setData(Object obj) {
        data = (byte[]) obj;
    }
}
