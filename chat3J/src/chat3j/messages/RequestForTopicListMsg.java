package chat3j.messages;
//토픽 목록을 요청하는 메시지, 반환타입은 토픽목록
public class RequestForTopicListMsg extends Message {
    // response
    public String[] topic;
    /*
    **토픽이름이외에 토픽에관한 정보 (토픽리스트 해시맵전달)
    */
    public RequestForTopicListMsg() {
    }
}
