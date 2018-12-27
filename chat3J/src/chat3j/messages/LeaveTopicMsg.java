package chat3j.messages;

/**
 * 클라이언트가 마스터에게 토픽을 떠나겠다는 의사를 밝히는 용도.
 *
 * 클라이언트 -> 마스터
 */
public class LeaveTopicMsg extends Message {

    /** request **/

    // 이 클라이언트가 들어가 있는 토픽의 리스트
    public String[] topics;
    public boolean close;

    /** response **/

    public boolean ok;

    public LeaveTopicMsg() {
        topics = null;
        close = false;
        ok = false;
    }
}
