package chat3j.messages;

/**
 * 클라이언트가 토픽을 생성하고 싶다는 의사를 마스터에게 전달하는 용도의 메시지.
 */
public class TopicCreationMsg extends Message {

    // request
    public String topic;
    public int tcp;
    public int udp;
    public String commType; // "Voice", "Chat"

    // response
    public boolean success;

    public TopicCreationMsg() {
        topic = null;
        tcp = 0;
        udp = 0;
        success = false;
    }
}
