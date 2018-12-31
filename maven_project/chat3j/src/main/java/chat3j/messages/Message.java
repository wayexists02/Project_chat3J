package chat3j.messages;

import com.esotericsoftware.kryonet.EndPoint;

public class Message {
    public static void registerMessage(EndPoint endPoint) {
        endPoint.getKryo().register(int[].class);
        endPoint.getKryo().register(Message.class);
        endPoint.getKryo().register(String.class);
        endPoint.getKryo().register(String[].class);
        endPoint.getKryo().register(EnterTopicMsg.class);
        endPoint.getKryo().register(LeaveTopicMsg.class);
        endPoint.getKryo().register(RequestTopicMsg.class);
        endPoint.getKryo().register(TopicCreationMsg.class);
        endPoint.getKryo().register(ReadyForEnterMsg.class);
        endPoint.getKryo().register(RequestForTopicListMsg.class);
        endPoint.getKryo().register(DisconnectToServerMsg.class);
    }

    public static void registerMessageForPublisher(EndPoint endPoint) {
        registerMessage(endPoint);
        endPoint.getKryo().register(byte[].class);
        endPoint.getKryo().register(VoiceDataMsg.class);
    }

    // 외부 요청에 의한 메시지의 경우, 이 정보는 반드시 메시지와 함께 가야함.
    public int optionId;
}
