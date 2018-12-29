package chat3j.messages;

import com.esotericsoftware.kryonet.EndPoint;

public class Message {
    public static void registerMessage(EndPoint endPoint) {
        endPoint.getKryo().register(int[].class);
        endPoint.getKryo().register(Message.class);
        endPoint.getKryo().register(String.class);
        endPoint.getKryo().register(EnterTopicMsg.class);
        endPoint.getKryo().register(String[].class);
        endPoint.getKryo().register(LeaveTopicMsg.class);
        endPoint.getKryo().register(RequestTopicMsg.class);
        endPoint.getKryo().register(TopicCreationMsg.class);
        endPoint.getKryo().register(ReadyForEnterMsg.class);

    }
}
