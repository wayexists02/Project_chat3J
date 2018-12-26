package chat3j.messages;

/**
 * 클라이언트가 마스터에게 토픽에 들어가겠다는 의사를 밝힌 후,
 * 마스터가 토픽이 있다는 것을 답신하고,
 * 클라이언트는 연결할 준비를 마치고 마스터에게 다시 이 메시지를 보냄.
 * 마스터는 이 메시지를 받고 토픽의 다른 노드들에게 EnterTopicMsg를 브로드캐스트
 */
public class ReadyForEnterMsg extends Message {

    // send to master
    public String topic;
    public int tcp;
    public int udp;

    public ReadyForEnterMsg() {
        this.tcp = 0;
        this.udp = 0;
    }
}
