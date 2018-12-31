package chat3j.messages;

/**
 * 클라이언트가 토픽에 들어가고 싶을때 최초로 마스터에게 보내는 요청메시지.
 */
public class RequestTopicMsg extends Message {

    // request
    public String topic;

    // response
    public boolean found;
    public String[] address;
    public int[] tcp;
    public int[] udp;
    public String commType; // "Voice", "Chat"

    public RequestTopicMsg() {
        topic = null;
        found = false;
        address = null;
        tcp = null;
        udp = null;
    }
}
